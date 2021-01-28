package be.tapped.vier.content

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.Validated
import arrow.core.computations.either
import arrow.core.extensions.either.applicative.applicative
import arrow.core.extensions.either.applicative.map
import arrow.core.extensions.list.traverse.sequence
import arrow.core.extensions.nonemptylist.semigroup.semigroup
import arrow.core.extensions.validated.applicative.applicative
import arrow.core.extensions.validated.bifunctor.mapLeft
import arrow.core.fix
import arrow.core.flatMap
import arrow.fx.coroutines.parTraverse
import be.tapped.common.internal.executeAsync
import be.tapped.common.internal.toValidateNel
import be.tapped.vier.ApiResponse.Failure
import be.tapped.vier.ApiResponse.Failure.HTML
import be.tapped.vier.ApiResponse.Failure.HTML.Parsing
import be.tapped.vier.ApiResponse.Success
import be.tapped.vier.common.safeAttr
import be.tapped.vier.common.safeBodyString
import be.tapped.vier.common.safeChild
import be.tapped.vier.common.safeSelect
import be.tapped.vier.common.safeSelectFirst
import be.tapped.vier.common.siteUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

internal class HtmlProgramParser(private val jsoupParser: JsoupParser) {
    internal suspend fun parse(html: String): Either<HTML, List<Program>> = either {
        println(html)
        val htmlPrograms = !jsoupParser.parse(html).safeSelect("a[data-program]")
        val jsons = htmlPrograms.map { !it.safeAttr("data-program").toEither() }
        jsons.map {
            Json {
                isLenient = true
                ignoreUnknownKeys = true
            }.decodeFromString(it)
        }
    }
}

internal class JsoupParser {
    fun parse(rawHtml: String): Document = Jsoup.parse(rawHtml)
}

internal class HtmlFullProgramParser(private val jsoupParser: JsoupParser) {
    private companion object {
        private const val datasetName = "data-hero"
        private const val CSSSelector = "div[$datasetName]"
    }

    fun canParse(html: String): Boolean = jsoupParser.parse(html).safeSelectFirst(CSSSelector).isRight()

    fun parse(html: String): Either<Failure, Program> =
        jsoupParser.parse(html).safeSelectFirst(CSSSelector).flatMap { it.safeAttr(datasetName).toEither() }.flatMap {
            Either.catch {
                val programDataObject = Json.decodeFromString<JsonObject>(it)["data"]!!.jsonObject
                Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                }.decodeFromJsonElement<Program>(programDataObject)
            }.mapLeft(Failure::JsonParsingException)
        }
}

public interface ProgramRepo {

    public suspend fun fetchPrograms(): Either<Failure, Success.Content.Programs>

    public suspend fun fetchProgram(programSearchKey: SearchHit.Source.SearchKey.Program): Either<Failure, Success.Content.SingleProgram>

}

internal class HttpProgramRepo(
    private val client: OkHttpClient,
    private val htmlProgramParser: HtmlProgramParser,
    private val htmlFullProgramParser: HtmlFullProgramParser,
) : ProgramRepo {

    // curl -X GET "https://www.goplay.be/"
    override suspend fun fetchPrograms(): Either<Failure, Success.Content.Programs> = withContext(Dispatchers.IO) {
        either {
            val html = !client.executeAsync(Request.Builder().get().url("$siteUrl/programmas").build()).safeBodyString()
            val programs = !htmlProgramParser.parse(html)
            Success.Content.Programs(programs)
        }
    }

    override suspend fun fetchProgram(programSearchKey: SearchHit.Source.SearchKey.Program): Either<Failure, Success.Content.SingleProgram> =
        fetchProgramFromUrl(programSearchKey.url).map(Success.Content::SingleProgram)

    private suspend fun fetchProgramFromUrl(programUrl: String): Either<Failure, Program> = either {
        val html = !withContext(Dispatchers.IO) {
            client.executeAsync(
                Request.Builder().get().url(programUrl).build()
            ).safeBodyString()
        }
        !htmlFullProgramParser.parse(html)
    }
}

package be.tapped.goplay.content

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.flatMap
import be.tapped.goplay.ApiResponse.Failure
import be.tapped.goplay.ApiResponse.Success
import be.tapped.goplay.common.ktorClient
import be.tapped.goplay.common.safeAttr
import be.tapped.goplay.common.safeSelectFirst
import be.tapped.goplay.common.siteUrl
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

internal class HtmlProgramParser(private val jsonSerializer: Json = be.tapped.goplay.common.jsonSerializer) {
    private val regex = "data-program=\"(.*)\"".toRegex()
    internal fun parse(html: String): Either<Failure, List<Program>> =
        Either.catch(regex.findAll(html).map { it.groupValues[1] }.map<String, Program>(jsonSerializer::decodeFromString)::toList).mapLeft(Failure::JsonParsingException)
}

internal class JsoupParser {
    fun parse(rawHtml: String): Document = Jsoup.parse(rawHtml)
}

internal class HtmlFullProgramParser(private val jsoupParser: JsoupParser, private val jsonSerializer: Json) {
    private companion object {
        private const val datasetName = "data-hero"
        private const val CSSSelector = "div[$datasetName]"
    }

    fun canParse(html: String): Boolean = jsoupParser.parse(html).safeSelectFirst(CSSSelector).isRight()

    fun parse(html: String): Either<Failure, Program> =
        jsoupParser.parse(html)
            .safeSelectFirst(CSSSelector)
            .flatMap { it.safeAttr(datasetName).toEither() }
            .flatMap {
                Either.catch {
                    val programDataObject = Json.decodeFromString<JsonObject>(it)["data"]!!.jsonObject
                    jsonSerializer.decodeFromJsonElement<Program>(programDataObject)
                }.mapLeft(Failure::JsonParsingException)
            }
}

public sealed interface ProgramRepo {
    public suspend fun fetchPrograms(): Either<Failure, Success.Content.Programs>
    public suspend fun fetchProgram(programSearchKey: SearchHit.Source.SearchKey.Program): Either<Failure, Success.Content.SingleProgram>
}

internal class HttpProgramRepo(
    private val client: HttpClient = ktorClient,
    private val htmlProgramParser: HtmlProgramParser = HtmlProgramParser(),
) : ProgramRepo {

    // Scrapes the https://www.goplay.be/programmas HTML content
    // curl -X GET "https://www.goplay.be/programmas"
    override suspend fun fetchPrograms(): Either<Failure, Success.Content.Programs> =
        withContext(Dispatchers.IO) {
            either {
                val html = client.get<HttpResponse>("$siteUrl/programmas").readText()
                val programs = htmlProgramParser.parse(html).bind()
                Success.Content.Programs(programs)
            }
        }

    override suspend fun fetchProgram(programSearchKey: SearchHit.Source.SearchKey.Program): Either<Failure, Success.Content.SingleProgram> {
        TODO("Not yet implemented")
    }
}

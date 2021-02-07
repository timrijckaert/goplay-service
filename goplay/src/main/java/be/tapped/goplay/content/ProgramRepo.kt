package be.tapped.goplay.content

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.flatMap
import arrow.core.left
import be.tapped.common.internal.executeAsync
import be.tapped.goplay.ApiResponse.Failure
import be.tapped.goplay.ApiResponse.Failure.HTML
import be.tapped.goplay.ApiResponse.Success
import be.tapped.goplay.common.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

internal class HtmlProgramParser(private val jsoupParser: JsoupParser) {
    internal suspend fun parse(html: String): Either<HTML, List<Program>> = either {
        val htmlPrograms = !jsoupParser.parse(html).safeSelect("a[data-program]")
        // The Program detail is found within the DOM as a JSON String
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
            jsoupParser.parse(html)
                    .safeSelectFirst(CSSSelector)
                    .flatMap { it.safeAttr(datasetName).toEither() }
                    .flatMap {
                        Either.catch {
                            val programDataObject = Json.decodeFromString<JsonObject>(it)["data"]!!.jsonObject
                            Json {
                                isLenient = true
                                ignoreUnknownKeys = true
                            }.decodeFromJsonElement<Program>(programDataObject)
                        }.mapLeft(Failure::JsonParsingException)
                    }
}

// The Search API from Vier is sometimes returning non available Programs.
// A redirect is triggered to a fixed location
// Model this into a soft error
internal class ProgramResponseValidator {
    private companion object {
        private const val NO_LONGER_AVAILABLE_REDIRECT_LOCATION = "$siteUrl/programma-niet-meer-beschikbaar"
    }

    internal suspend fun validateResponse(response: Response): Either<Failure, String> =
            if (response.priorResponse?.headers("Location")?.firstOrNull() == NO_LONGER_AVAILABLE_REDIRECT_LOCATION) {
                Failure.Content.ProgramNoLongerAvailable.left()
            } else {
                response.safeBodyString()
            }
}

public sealed interface ProgramRepo {

    public suspend fun fetchPrograms(): Either<Failure, Success.Content.Programs>

    public suspend fun fetchProgram(programSearchKey: SearchHit.Source.SearchKey.Program): Either<Failure, Success.Content.SingleProgram>

}

internal class HttpProgramRepo(
        private val client: OkHttpClient,
        private val htmlProgramParser: HtmlProgramParser,
        private val htmlFullProgramParser: HtmlFullProgramParser,
        private val programResponseValidator: ProgramResponseValidator,
) : ProgramRepo {

    // Scrapes the https://www.goplay.be/programmas searching for all available Programs and the details associated with it.
    // Fortunately for us the details of a Program is encoded in the HTML DOM as a JSON
    // curl -X GET "https://www.goplay.be/programmas"
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
        val html = withContext(Dispatchers.IO) {
            val response = client.executeAsync(Request.Builder().get().url(programUrl).build())
            !programResponseValidator.validateResponse(response)
        }
        !htmlFullProgramParser.parse(html)
    }
}

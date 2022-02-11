package be.tapped.goplay.content

import arrow.core.Either
import arrow.core.computations.either
import be.tapped.goplay.ApiResponse.Failure
import be.tapped.goplay.ApiResponse.Success
import be.tapped.goplay.siteUrl
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

internal class HtmlJsonProgramExtractor(private val jsonSerializer: Json) {
    private val regex = "data-program=\"([^\"]+)\"".toRegex()
    internal fun parse(html: String): Either<Failure, List<Program>> =
        Either.catch(
            regex.findAll(html)
                .map { it.groupValues[1] }
                .map(String::htmlDecode)
                .map<String, Program>(jsonSerializer::decodeFromString)::toList
        ).mapLeft(Failure::JsonParsingException)
}

internal fun interface ProgramRepo {
    suspend fun fetchPrograms(): Either<Failure, Success.Content.Programs>
}

internal fun httpProgramRepo(client: HttpClient, htmlJsonProgramExtractor: HtmlJsonProgramExtractor): ProgramRepo = ProgramRepo {
    withContext(Dispatchers.IO) {
        either {
            val html = client.get<HttpResponse>("$siteUrl/programmas").readText()
            val programs = htmlJsonProgramExtractor.parse(html).bind()
            Success.Content.Programs(programs)
        }
    }
}

// A poor man's html decoder
// TODO refactor or replace with a dedicated MPP lib?
private fun String.htmlDecode(): String {
    var s = this
    if (s.isEmpty()) {
        return s
    }
    s = s.replace("&nbsp;", " ")
    s = s.replace("&quot;", "\"")
    s = s.replace("&apos;", "'")
    s = s.replace("&#39;", "'")
    s = s.replace("&lt;", "<")
    s = s.replace("&gt;", ">")
    s = s.replace("&amp;", "&")

    // whitespace patterns
    val zeroOrMoreWhitespaces = "\\s*?"
    val oneOrMoreWhitespaces = "\\s+?"

    // replace <br/> by \n
    s = s.replace(
        "<" + zeroOrMoreWhitespaces + "br" + zeroOrMoreWhitespaces + "/" + zeroOrMoreWhitespaces + ">".toRegex(),
        "\n"
    )
    // replace HTML-tabs by \t
    s = s.replace(
        ("<" + zeroOrMoreWhitespaces + "span" + oneOrMoreWhitespaces + "style"
                + zeroOrMoreWhitespaces + "=" + zeroOrMoreWhitespaces + "\"white-space:pre\""
                + zeroOrMoreWhitespaces + ">&#9;<" + zeroOrMoreWhitespaces + "/" + zeroOrMoreWhitespaces + "span"
                + zeroOrMoreWhitespaces + ">").toRegex(), "\t"
    )
    return s
}

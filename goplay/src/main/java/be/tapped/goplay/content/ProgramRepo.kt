package be.tapped.goplay.content

import arrow.core.Either
import arrow.core.Either.Companion.catch
import arrow.core.Nel
import arrow.core.computations.either
import be.tapped.goplay.ApiResponse.Failure
import be.tapped.goplay.ApiResponse.Success
import be.tapped.goplay.epg.GoPlayBrand
import be.tapped.goplay.safeDecodeFromJsonElement
import be.tapped.goplay.safeDecodeFromString
import be.tapped.goplay.safeGet
import be.tapped.goplay.safeReadText
import be.tapped.goplay.siteUrl
import be.tapped.goplay.toNel
import io.ktor.client.HttpClient
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

internal interface ProgramRepo {
    suspend fun fetchPrograms(): Either<Failure, Success.Content.Program.Overview>
    suspend fun fetchProgramByLink(link: Program.Link): Either<Failure, Success.Content.Program.Detail>
    suspend fun fetchProgramById(id: Program.Id): Either<Failure, Success.Content.Program.Detail>
    suspend fun fetchPopularPrograms(brand: GoPlayBrand? = null): Either<Failure, Nel<Success.Content.Program.Detail>>
}

internal class HttpProgramRepo(
    private val client: HttpClient,
    private val jsonSerializer: Json,
    private val allProgramsHtmlJsonExtractor: AllProgramsHtmlJsonExtractor,
    private val programDetailHtmlJsonExtractor: ProgramDetailHtmlJsonExtractor,
) : ProgramRepo {

    override suspend fun fetchPrograms(): Either<Failure, Success.Content.Program.Overview> =
        withContext(Dispatchers.IO) {
            either {
                val html = client.safeGet<HttpResponse>("$siteUrl/programmas").bind().safeReadText().bind()
                val jsonPrograms = allProgramsHtmlJsonExtractor.parse(html).bind()
                val programs = jsonPrograms.map { jsonSerializer.safeDecodeFromString<Program.Overview>(it).bind() }.toNel { Failure.Content.NoPrograms }.bind()
                Success.Content.Program.Overview(programs)
            }
        }

    override suspend fun fetchProgramByLink(link: Program.Link): Either<Failure, Success.Content.Program.Detail> =
        withContext(Dispatchers.IO) {
            either {
                val html = client.safeGet<HttpResponse>("$siteUrl${link.link}").bind().safeReadText().bind()
                val jsonProgram = programDetailHtmlJsonExtractor.parse(html).bind()
                val dataObj = catch { jsonSerializer.safeDecodeFromString<JsonObject>(jsonProgram).bind().getValue("data") }.mapLeft(Failure::JsonParsingException).bind()
                val program = jsonSerializer.safeDecodeFromJsonElement<Program.Detail>(dataObj).bind()
                Success.Content.Program.Detail(program)
            }
        }

    override suspend fun fetchProgramById(id: Program.Id): Either<Failure, Success.Content.Program.Detail> =
        withContext(Dispatchers.IO) { either { Success.Content.Program.Detail(client.safeGet<Program.Detail>("$siteUrl/api/program/${id.id}").bind()) } }

    override suspend fun fetchPopularPrograms(brand: GoPlayBrand?): Either<Failure, Nel<Success.Content.Program.Detail>> =
        withContext(Dispatchers.IO) {
            either {
                client.safeGet<List<Program.Detail>>(
                    "$siteUrl/api/programs/popular/" +
                            when (brand) {
                                GoPlayBrand.Play4 -> "vier"
                                GoPlayBrand.Play5 -> "vijf"
                                GoPlayBrand.Play6 -> "zes"
                                GoPlayBrand.Play7 -> "zeven"
                                null -> ""
                            }
                )
                    .bind()
                    .map(Success.Content.Program::Detail)
                    .toNel { Failure.Content.NoPrograms }.bind()
            }
        }
}

internal class ProgramDetailHtmlJsonExtractor {
    private val regex by lazy("data-hero=\"([^\"]+)"::toRegex)
    internal fun parse(html: String): Either<Failure.HTMLJsonExtractionException, String> =
        catch {
            val (htmlEncodedJson) = regex.find(html)!!.destructured
            htmlEncodedJson.htmlDecode()
        }.mapLeft(Failure::HTMLJsonExtractionException)
}

internal class AllProgramsHtmlJsonExtractor {
    private val regex by lazy("data-program=\"([^\"]+)\""::toRegex)
    internal fun parse(html: String): Either<Failure, List<String>> =
        catch(regex.findAll(html).map { it.groupValues[1] }.map(String::htmlDecode)::toList).mapLeft(Failure::HTMLJsonExtractionException)
}

// A poor man's HTML decoder
// Shameless port of http://www.java2s.com/example/java-utility-method/html-decode/htmldecode-string-s-eb5ed.html
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

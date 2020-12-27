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
import arrow.core.left
import arrow.fx.coroutines.parTraverse
import be.tapped.common.internal.executeAsync
import be.tapped.common.internal.toValidateNel
import be.tapped.vier.ApiResponse
import be.tapped.vier.ApiResponse.Failure
import be.tapped.vier.ApiResponse.Failure.HTML
import be.tapped.vier.ApiResponse.Failure.HTML.Parsing
import be.tapped.vier.ApiResponse.Success
import be.tapped.vier.common.safeAttr
import be.tapped.vier.common.safeBodyString
import be.tapped.vier.common.safeChild
import be.tapped.vier.common.safeSelect
import be.tapped.vier.common.safeSelectFirst
import be.tapped.vier.common.safeText
import be.tapped.vier.common.vierApiUrl
import be.tapped.vier.common.vierUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

internal data class PartialProgram(val name: String, val path: String)

internal class HtmlPartialProgramParser(private val jsoupParser: JsoupParser) {

    private val applicative = Validated.applicative(NonEmptyList.semigroup<HTML>())

    internal suspend fun parse(html: String): Either<HTML, List<PartialProgram>> =
        jsoupParser.parse(html).safeSelect("a.program-overview__link").flatMap { links ->
            links.map { link ->
                val path = link.safeAttr("href").toValidatedNel()
                val title = link.safeChild(0).flatMap { it.safeText() }.toValidateNel()
                applicative.mapN(title, path) { (title, path) -> PartialProgram(title, path) }
            }.sequence(applicative)
                .mapLeft { Parsing(it) }
                .map { it.fix() }
                .toEither()
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

    suspend fun parse(html: String): Either<Failure, Program> =
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

internal class HtmlClipEpisodeParser(private val jsoupParser: JsoupParser) {
    private companion object {
        private const val datasetName = "data-video"
        private const val CSSSelector = "div[$datasetName]"
    }

    fun canParse(html: String): Boolean = jsoupParser.parse(html).safeSelectFirst(CSSSelector).isRight()

    fun parse(html: String): Either<Failure, EpisodeUuid> =
        jsoupParser.parse(html)
            .safeSelectFirst(CSSSelector)
            .flatMap { it.safeAttr(datasetName).toEither() }
            .flatMap {
                Either.catch { EpisodeUuid(Json.decodeFromString<JsonObject>(it)["id"]!!.jsonPrimitive.content) }
                    .mapLeft(Failure::JsonParsingException)
            }
}

internal class EpisodeParser {
    fun parse(json: String): Either<ApiResponse.Failure, Program.Playlist.Episode> =
        Either.catch { Json.decodeFromString<Program.Playlist.Episode>(json) }.mapLeft(Failure::JsonParsingException)
}

public interface ProgramRepo {

    public suspend fun fetchPrograms(): Either<Failure, Success.Content.Programs>

    public suspend fun fetchProgram(programSearchKey: SearchHit.Source.SearchKey.Program): Either<Failure, Success.Content.SingleProgram>

    public suspend fun fetchEpisode(episodeByNodeIdSearchKey: SearchHit.Source.SearchKey.EpisodeByNodeId): Either<Failure, Success.Content.SingleEpisode>

    public suspend fun fetchEpisode(episodeVideoUuid: EpisodeUuid): Either<Failure, Success.Content.SingleEpisode>

}

internal class HttpProgramRepo(
    private val client: OkHttpClient,
    private val htmlPartialProgramParser: HtmlPartialProgramParser,
    private val htmlFullProgramParser: HtmlFullProgramParser,
    private val htmlClipEpisodeParser: HtmlClipEpisodeParser,
    private val episodeParser: EpisodeParser,
) : ProgramRepo {

    // curl -X GET "https://www.vier.be/"
    override suspend fun fetchPrograms(): Either<Failure, Success.Content.Programs> {
        suspend fun fetchProgramDetails(partialPrograms: List<PartialProgram>): Either<Failure, List<Program>> =
            partialPrograms.parTraverse(Dispatchers.IO) { fetchProgramFromUrl("$vierUrl${it.path}") }
                .sequence(Either.applicative())
                .map { it.fix() }

        return withContext(Dispatchers.IO) {
            either {
                val html = !client.executeAsync(
                    Request.Builder()
                        .get()
                        .url(vierUrl)
                        .build()
                ).safeBodyString()

                val partialPrograms = !htmlPartialProgramParser.parse(html)
                val programs = !fetchProgramDetails(partialPrograms)
                Success.Content.Programs(programs)
            }
        }
    }

    private suspend fun fetchProgramFromUrl(programUrl: String): Either<Failure, Program> =
        either {
            val html = !fetchRawResponse(programUrl)
            !htmlFullProgramParser.parse(html)
        }

    private suspend fun fetchRawResponse(programUrl: String): Either<Failure, String> =
        withContext(Dispatchers.IO) {
            client.executeAsync(
                Request.Builder()
                    .get()
                    .url(programUrl)
                    .build()
            ).safeBodyString()
        }

    override suspend fun fetchProgram(programSearchKey: SearchHit.Source.SearchKey.Program): Either<Failure, Success.Content.SingleProgram> =
        fetchProgramFromUrl(programSearchKey.url).map(Success.Content::SingleProgram)

    override suspend fun fetchEpisode(episodeVideoUuid: EpisodeUuid): Either<Failure, Success.Content.SingleEpisode> =
        withContext(Dispatchers.IO) {
            either {
                val episode = !client.executeAsync(
                    Request.Builder()
                        .get()
                        .url("$vierApiUrl/video/${episodeVideoUuid.id}")
                        .build()
                ).safeBodyString()

                Success.Content.SingleEpisode(!episodeParser.parse(episode))
            }
        }

    private suspend fun fetchEpisodeFromProgramHtml(nodeId: String, programHtml: String): Either<Failure, Success.Content.SingleEpisode> =
        either {
            val program = !htmlFullProgramParser.parse(programHtml)
            val episodeForSearchKey =
                !Either
                    .fromNullable(
                        program
                            .playlists
                            .flatMap(Program.Playlist::episodes)
                            .firstOrNull { it.pageInfo.nodeId == nodeId }
                    )
                    .mapLeft { Failure.Content.NoEpisodeFound }
            Success.Content.SingleEpisode(episodeForSearchKey)
        }

    enum class EpisodeType {
        CLIP,
        FULL_EPISODE,
        UNKNOWN
    }

    private fun determineEpisodeType(html: String): EpisodeType =
        when {
            htmlFullProgramParser.canParse(html) -> EpisodeType.FULL_EPISODE
            htmlClipEpisodeParser.canParse(html) -> EpisodeType.CLIP
            else                                 -> EpisodeType.UNKNOWN
        }

    override suspend fun fetchEpisode(episodeByNodeIdSearchKey: SearchHit.Source.SearchKey.EpisodeByNodeId): Either<Failure, Success.Content.SingleEpisode> {
        return either {
            val html = !fetchRawResponse(episodeByNodeIdSearchKey.url)
            !when (determineEpisodeType(html)) {
                EpisodeType.CLIP -> fetchEpisode(!htmlClipEpisodeParser.parse(html))
                EpisodeType.FULL_EPISODE -> fetchEpisodeFromProgramHtml(episodeByNodeIdSearchKey.nodeId, html)
                EpisodeType.UNKNOWN -> Failure.Content.NoEpisodeFound.left()
            }
        }
    }
}

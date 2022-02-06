package be.tapped.goplay.content

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.flatMap
import arrow.core.left
import be.tapped.common.internal.executeAsync
import be.tapped.goplay.ApiResponse
import be.tapped.goplay.common.safeAttr
import be.tapped.goplay.common.safeBodyString
import be.tapped.goplay.common.safeSelectFirst
import be.tapped.goplay.common.siteUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request

internal class EpisodeParser {
    fun parse(json: String): Either<ApiResponse.Failure, Program.Playlist.Episode> = Either.catch {
        Json {
            ignoreUnknownKeys = true
        }.decodeFromString<Program.Playlist.Episode>(json)
    }.mapLeft(ApiResponse.Failure::JsonParsingException)
}

internal class HtmlClipEpisodeParser(private val jsoupParser: JsoupParser) {
    private companion object {
        private const val datasetName = "data-video"
        private const val CSSSelector = "div[$datasetName]"
    }

    fun canParse(html: String): Boolean = jsoupParser.parse(html).safeSelectFirst(CSSSelector).isRight()

    fun parse(html: String): Either<ApiResponse.Failure, EpisodeUuid> =
        jsoupParser.parse(html).safeSelectFirst(CSSSelector).flatMap { it.safeAttr(datasetName).toEither() }.flatMap {
            Either.catch { EpisodeUuid(Json.decodeFromString<JsonObject>(it)["id"]!!.jsonPrimitive.content) }
                .mapLeft(ApiResponse.Failure::JsonParsingException)
        }
}

public sealed interface EpisodeRepo {

    public suspend fun fetchEpisode(episodeVideoUuid: EpisodeUuid): Either<ApiResponse.Failure, ApiResponse.Success.Content.SingleEpisode>

    public suspend fun fetchEpisode(episodeByNodeIdSearchKey: SearchHit.Source.SearchKey.EpisodeByNodeId): Either<ApiResponse.Failure, ApiResponse.Success.Content.SingleEpisode>

}

internal class HttpEpisodeRepo(
    private val client: OkHttpClient,
    private val htmlFullProgramParser: HtmlFullProgramParser,
    private val htmlClipEpisodeParser: HtmlClipEpisodeParser,
    private val episodeParser: EpisodeParser,
) : EpisodeRepo {

    private suspend fun fetchRawResponse(programUrl: String): Either<ApiResponse.Failure, String> =
        withContext(Dispatchers.IO) { client.executeAsync(Request.Builder().get().url(programUrl).build()).safeBodyString() }

    enum class EpisodeType {
        CLIP,
        FULL_EPISODE,
        UNKNOWN
    }

    private fun determineEpisodeType(html: String): EpisodeType = when {
        htmlFullProgramParser.canParse(html) -> EpisodeType.FULL_EPISODE
        htmlClipEpisodeParser.canParse(html) -> EpisodeType.CLIP
        else -> EpisodeType.UNKNOWN
    }

    private suspend fun fetchEpisodeFromProgramHtml(
        nodeId: String,
        programHtml: String,
    ): Either<ApiResponse.Failure, ApiResponse.Success.Content.SingleEpisode> = either {
        val program = htmlFullProgramParser.parse(programHtml).bind()
        val episodeForSearchKey =
            Either.fromNullable(program.playlists.flatMap(Program.Playlist::episodes).firstOrNull { it.pageInfo.nodeId == nodeId })
                .mapLeft { ApiResponse.Failure.Content.NoEpisodeFound }.bind()
        ApiResponse.Success.Content.SingleEpisode(episodeForSearchKey)
    }

    // GoPlay API does not make a distinction between clips and video's. However the strategy for fetching the Episode data from the HTML differs.
    override suspend fun fetchEpisode(episodeByNodeIdSearchKey: SearchHit.Source.SearchKey.EpisodeByNodeId): Either<ApiResponse.Failure, ApiResponse.Success.Content.SingleEpisode> {
        return either {
            val html = fetchRawResponse(episodeByNodeIdSearchKey.url).bind()
            when (determineEpisodeType(html)) {
                EpisodeType.CLIP -> fetchEpisode(htmlClipEpisodeParser.parse(html).bind())
                EpisodeType.FULL_EPISODE -> fetchEpisodeFromProgramHtml(episodeByNodeIdSearchKey.nodeId, html)
                EpisodeType.UNKNOWN -> ApiResponse.Failure.Content.NoEpisodeFound.left()
            }.bind()
        }
    }

    override suspend fun fetchEpisode(episodeVideoUuid: EpisodeUuid): Either<ApiResponse.Failure, ApiResponse.Success.Content.SingleEpisode> =
        withContext(Dispatchers.IO) {
            either {
                val episode = client.executeAsync(
                    Request.Builder().get().url("$siteUrl/api/video/${episodeVideoUuid.id}").build()
                ).safeBodyString().bind()

                ApiResponse.Success.Content.SingleEpisode(episodeParser.parse(episode).bind())
            }
        }

}

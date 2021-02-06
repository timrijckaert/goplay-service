package be.tapped.vrtnu.content

import arrow.core.*
import arrow.core.computations.either
import arrow.fx.coroutines.parTraverse
import be.tapped.common.internal.executeAsync
import be.tapped.vrtnu.ApiResponse
import be.tapped.vrtnu.common.corporateSiteUrl
import be.tapped.vrtnu.common.safeBodyString
import be.tapped.vrtnu.common.siteUrl
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.Exception

// https://www.vrt.be/vrtnu/a-z/het-journaal/jcr:content/parsys/container.model.json
// https://www.vrt.be/vrtnu/a-z/merlina/jcr:content/parsys/container.model.json
internal class SeasonRepo(private val episodeRepo: EpisodeRepo) {
    suspend fun seasonsFromAEMJson(json: String): List<Either<ApiResponse.Failure, Season>> {
        val seasons = Json
                .decodeFromString<JsonObject>(json)
                .getValue(":items").jsonObject
                .getValue("banner").jsonObject
                .getValue(":items").jsonObject
        val seasonKeys = seasons.keys.filter { it != "navigation" }
        return seasonKeys.parTraverse { seasonKey ->
            either {
                val season = seasons.getValue(seasonKey).jsonObject
                val episodesForSeason = !episodeRepo.fetchEpisodesForEpisode(seasonKey, season)
                Season(seasonKey, episodesForSeason)
            }
        }
    }
}

internal class EpisodeRepo(private val client: OkHttpClient,
                           private val episodeParser: EpisodeParser) {
    private companion object {
        private const val EPISODES_KEY: String = ":items"
    }

    suspend fun fetchEpisodesForEpisode(seasonKey: String, season: JsonObject): Either<ApiResponse.Failure, List<Season.Episode>> =
            either {
                !try {
                    val episodes =
                            !(season[EPISODES_KEY]?.jsonObject?.right()
                                    ?: fetchEpisodesForSeason(season.getValue("lazySrc").jsonPrimitive.content))
                    episodeParser.parse(episodes).right()
                } catch (ex: Exception) {
                    ApiResponse.Failure.Content.NoEpisodesFound(ex, seasonKey).left()
                }
            }

    private suspend fun fetchEpisodesForSeason(lazySrc: String): Either<ApiResponse.Failure, JsonObject> =
            either {
                val rawJson = !client.executeAsync(Request.Builder().get().url("$corporateSiteUrl$lazySrc").build()).safeBodyString()
                !Json.decodeFromString<JsonObject>(rawJson).getValue(EPISODES_KEY).jsonObject.right()
            }
}

internal class EpisodeParser(private val urlPrefixMapper: UrlPrefixMapper) {
    fun parse(episodesJson: JsonObject): List<Season.Episode> =
            episodesJson.keys.map { episodeKey ->
                val episode = episodesJson.getValue(episodeKey).jsonObject
                val title = episode.getValue("title").jsonPrimitive.content
                val image = urlPrefixMapper.toHttpsUrl(episode.getValue("image").jsonObject.getValue("src").jsonPrimitive.content)
                val description = episode.getValue("description").jsonPrimitive.content
                val actions = episode.getValue("actions").jsonArray.filterIsInstance<JsonObject>().first().jsonObject
                val videoId = VideoId(actions.getValue("episodeVideoId").jsonPrimitive.content)
                val publicationId = PublicationId(actions.getValue("episodePublicationId").jsonPrimitive.content)
                val duration = episode.getValue("mediaMeta").jsonArray.filterIsInstance<JsonObject>().first { it.getValue("type").jsonPrimitive.content == "default" }.jsonObject.getValue("value").jsonPrimitive.content
                Season.Episode(
                        title,
                        image,
                        description,
                        videoId,
                        publicationId,
                        duration,
                )
            }
}

public interface PlaylistRepo {
    public suspend fun fetchProgramPlaylist(program: Program): Either<NonEmptyList<ApiResponse.Failure>, List<Season>>
}

internal class AEMPlaylistRepo(private val client: OkHttpClient,
                               private val seasonRepo: SeasonRepo) : PlaylistRepo {

    override suspend fun fetchProgramPlaylist(program: Program): Either<NonEmptyList<ApiResponse.Failure>, List<Season>> {
        val json: Either<ApiResponse.Failure, List<Either<ApiResponse.Failure, Season>>> = client.executeAsync(
                Request.Builder()
                        .get()
                        .url("$siteUrl/a-z/${program.programName}/jcr:content/parsys/container.model.json")
                        .build()
        ).safeBodyString().map {
            seasonRepo.seasonsFromAEMJson(it)
        }
        TODO()
    }
}


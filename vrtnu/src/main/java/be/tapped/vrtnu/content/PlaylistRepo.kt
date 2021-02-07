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

// https://www.vrt.be/vrtnu/a-z/het-journaal/jcr:content/parsys/container.model.json
// https://www.vrt.be/vrtnu/a-z/merlina/jcr:content/parsys/container.model.json
internal class SeasonRepo(private val episodeRepo: EpisodeRepo) {
    suspend fun seasonsFromAEMJson(json: String): Either<ApiResponse.Failure, List<Season>> {
        val seasons = Either.catch {
            // The AEM Json output is the opposite of stable.
            val decodedJson = Json.decodeFromString<JsonObject>(json).getValue(":items").jsonObject
            decodedJson["banner"]?.jsonObject?.get(":items")?.jsonObject
                    ?: decodedJson["banner_copy"]?.jsonObject?.get(":items")?.jsonObject
                    ?: decodedJson["episodes-list"]?.jsonObject?.getValue(":items")?.jsonObject
                    ?: decodedJson.getValue("episodes_list").jsonObject.getValue(":items").jsonObject
        }.mapLeft(ApiResponse.Failure::JsonParsingException)
        return seasons
                .flatMap { it ->
                    val seasonKeys = it.keys.filter { it != "navigation" }
                    seasonKeys.parTraverse<String, Either<ApiResponse.Failure, Season>> { seasonKey ->
                        either {
                            val season = it.getValue(seasonKey).jsonObject
                            val episodesForSeason = !episodeRepo.fetchEpisodesForEpisode(seasonKey, season)
                            Season(seasonKey, episodesForSeason)
                        }
                    }.sequenceEither()
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
                    episodeParser.parse(episodes)
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
    fun parse(episodesJson: JsonObject): Either<ApiResponse.Failure, List<Season.Episode>> =
            Either.catch {
                episodesJson.keys.map { episodeKey ->
                    val episode = episodesJson.getValue(episodeKey).jsonObject
                    val meta = episode.getValue("meta").jsonArray.filterIsInstance<JsonObject>()
                    val title = calculateEpisodeTitle(episode, meta)
                    val image = urlPrefixMapper.toHttpsUrl(episode.getValue("image").jsonObject.getValue("src").jsonPrimitive.content)
                    val description = episode["description"]?.jsonPrimitive?.content
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
            }.mapLeft(ApiResponse.Failure::JsonParsingException)

    private fun calculateEpisodeTitle(episode: JsonObject, meta: List<JsonObject>): String {
        fun alternativeTitle(meta: List<JsonObject>): String =
                meta.map { it.getValue("value") }.joinToString(separator = " ")

        return episode["title"]?.jsonPrimitive?.content ?: alternativeTitle(meta)
    }
}

public interface PlaylistRepo {
    public suspend fun fetchProgramPlaylist(program: Program): Either<ApiResponse.Failure, List<Season>>
}

internal class AEMPlaylistRepo(private val client: OkHttpClient,
                               private val seasonRepo: SeasonRepo) : PlaylistRepo {

    override suspend fun fetchProgramPlaylist(program: Program): Either<ApiResponse.Failure, List<Season>> =
            either {
                val json = !client.executeAsync(
                        Request.Builder()
                                .get()
                                .url("$siteUrl/a-z/${program.programName}/jcr:content/parsys/container.model.json")
                                .build()
                ).safeBodyString()
                !seasonRepo.seasonsFromAEMJson(json)
            }
}


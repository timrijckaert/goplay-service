package be.tapped.vrtnu.content

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import be.tapped.vrtnu.ApiResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*

// https://www.vrt.be/vrtnu/a-z/het-journaal/jcr:content/parsys/container.model.json
// https://www.vrt.be/vrtnu/a-z/merlina/jcr:content/parsys/container.model.json
internal class AEMEpisodeJsonParser(private val urlPrefixMapper: UrlPrefixMapper) {
    public fun parse(json: String): Either<ApiResponse.Failure, List<Season>> {
        return try {
            val jsonObject = Json {}.decodeFromString<JsonObject>(json)
            val seasons = jsonObject.getValue(":items").jsonObject.getValue("banner").jsonObject.getValue(":items").jsonObject
            val seasonKeys = seasons.keys.filter { it != "navigation" }
            seasonKeys.map { seasonKey ->
                val season = seasons.getValue(seasonKey).jsonObject
                val episodes = season[":items"]?.jsonObject
                if (episodes != null) {
                    Season.CurrentSeason(
                            seasonKey,
                            episodes.keys.map { episodeKey ->
                                val episode = episodes.getValue(episodeKey).jsonObject
                                val title = episode.getValue("title").jsonPrimitive.content
                                val image = urlPrefixMapper.toHttpsUrl(episode.getValue("image").jsonObject.getValue("src").jsonPrimitive.content)
                                val description = episode.getValue("description").jsonPrimitive.content
                                val actions = episode.getValue("actions").jsonArray.filterIsInstance<JsonObject>().first().jsonObject
                                val videoId = VideoId(actions.getValue("episodeVideoId").jsonPrimitive.content)
                                val publicationId = PublicationId(actions.getValue("episodePublicationId").jsonPrimitive.content)
                                val duration = episode.getValue("mediaMeta").jsonArray.filterIsInstance<JsonObject>().first { it.getValue("type").jsonPrimitive.content == "default" }.jsonObject.getValue("value").jsonPrimitive.content
                                Episode(
                                        title,
                                        image,
                                        description,
                                        videoId,
                                        publicationId,
                                        duration,
                                )
                            }
                    )
                } else {
                    Season.LazySeason(season.getValue("lazySrc").jsonPrimitive.content)
                }
            }.right()
        } catch (e: Exception) {
            ApiResponse.Failure.JsonParsingException(e).left()
        }
    }
}

public data class Episode(
        val title: String,
        val image: String,
        val description: String,
        val videoId: VideoId,
        val publicationId: PublicationId,
        val duration: String,
)

public sealed class Season {
    public data class CurrentSeason(val name: String, val episodes: List<Episode>) : Season()
    public data class LazySeason(val lazySrc: String) : Season()
}

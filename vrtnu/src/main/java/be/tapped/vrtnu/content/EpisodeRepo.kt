package be.tapped.vrtnu.content

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.flatMap
import be.tapped.vtmgo.common.executeAsync
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

internal class JsonEpisodeParser {
    suspend fun parse(json: String): Either<ApiResponse.Failure, List<Episode>> =
        Either.fromNullable(Json.decodeFromString<JsonObject>(json)["results"]?.jsonArray)
            .mapLeft { ApiResponse.Failure.EmptyJson }
            .flatMap {
                Either.catch {
                    Json.decodeFromJsonElement<List<Episode>>(it)
                }.mapLeft {
                    ApiResponse.Failure.JsonParsingException(it)
                }
            }
}

interface EpisodeRepo {
    suspend fun fetchEpisodeByCategory(category: Category): Either<ApiResponse.Failure, ApiResponse.Success.Episodes>
}

internal class HttpEpisodeRepo(
    private val client: OkHttpClient,
    private val jsonEpisodeParser: JsonEpisodeParser,
) : EpisodeRepo {

    companion object {
        // TODO fetch all results if paginated
        private const val CATEGORY_URL = "https://vrtnu-api.vrt.be/search?size=150&facets[categories]"
    }

    override suspend fun fetchEpisodeByCategory(category: Category): Either<ApiResponse.Failure, ApiResponse.Success.Episodes> {
        val url = "$CATEGORY_URL=${category.name}"

        val episodeByCategoryResponse = client.executeAsync(
            Request.Builder()
                .get()
                .url(url.toHttpUrl())
                .build()
        )

        return either {
            val rawJson = !Either.fromNullable(episodeByCategoryResponse.body).mapLeft { ApiResponse.Failure.EmptyJson }
            ApiResponse.Success.Episodes(!jsonEpisodeParser.parse(rawJson.string()))
        }
    }
}

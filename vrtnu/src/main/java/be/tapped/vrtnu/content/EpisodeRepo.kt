package be.tapped.vrtnu.content

import arrow.core.Either
import arrow.core.computations.either
import be.tapped.vrtnu.content.ApiResponse.Failure.JsonParsingException
import be.tapped.vtmgo.common.executeAsync
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

internal class JsonEpisodeParser {
    suspend fun parse(json: String): Either<ApiResponse.Failure, ElasticSearchResult<Episode>> =
        Either
            .catch { Json.decodeFromString<ElasticSearchResult<Episode>>(json) }
            .mapLeft(::JsonParsingException)
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
            val episodes = (!jsonEpisodeParser.parse(rawJson.string())).results
            ApiResponse.Success.Episodes(episodes)
        }
    }
}

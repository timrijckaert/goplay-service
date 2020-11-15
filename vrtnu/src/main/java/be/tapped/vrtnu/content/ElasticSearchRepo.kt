package be.tapped.vrtnu.content

import arrow.core.Either
import arrow.core.computations.either
import be.tapped.vrtnu.content.ApiResponse.Failure.JsonParsingException
import be.tapped.vrtnu.content.ElasticSearchRepo.Companion.DEFAULT_SEARCH_QUERY_INDEX
import be.tapped.vrtnu.content.ElasticSearchRepo.Companion.DEFAULT_SEARCH_QUERY_ORDER
import be.tapped.vtmgo.common.executeAsync
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

internal class JsonEpisodeParser {
    suspend fun parse(json: String): Either<ApiResponse.Failure, ElasticSearchResult<Episode>> =
        Either
            .catch { Json.decodeFromString<ElasticSearchResult<Episode>>(json) }
            .mapLeft(::JsonParsingException)
}

interface ElasticSearchRepo {
    companion object {
        const val DEFAULT_SEARCH_SIZE = 150
        private const val MAX_SEARCH_SIZE = 300

        val DEFAULT_SEARCH_QUERY_INDEX = SearchQuery.Index.VIDEO
        val DEFAULT_SEARCH_QUERY_ORDER = SearchQuery.Order.DESC
    }

    // https://github.com/add-ons/plugin.video.vrt.nu/wiki/VRT-NU-API#vrt-api-parameters
    data class SearchQuery(
        val size: Int = DEFAULT_SEARCH_SIZE,
        val index: Index = DEFAULT_SEARCH_QUERY_INDEX,
        val order: Order = DEFAULT_SEARCH_QUERY_ORDER,
        val available: Boolean? = null,
        val query: String? = null,
        val category: String? = null,
        val start: Long? = null,
        val end: Long? = null,
        val custom: Map<String, String> = emptyMap(),
    ) {
        init {
            if (size > MAX_SEARCH_SIZE) {
                throw IllegalArgumentException("search size can not be bigger than $MAX_SEARCH_SIZE")
            }
        }

        enum class Order {
            ASC,
            DESC;
        }

        enum class Index {
            VIDEO,
            CORPORATE
        }
    }

    suspend fun search(searchQuery: SearchQuery): Flow<Either<ApiResponse.Failure, ApiResponse.Success.Episodes>>
}

internal class HttpElasticSearchRepo(
    private val client: OkHttpClient,
    private val jsonEpisodeParser: JsonEpisodeParser,
) : ElasticSearchRepo {

    companion object {
        private const val START_PAGE_INDEX = 1
    }

    override suspend fun search(searchQuery: ElasticSearchRepo.SearchQuery): Flow<Either<ApiResponse.Failure, ApiResponse.Success.Episodes>> =
        flow<Either<ApiResponse.Failure, ApiResponse.Success.Episodes>> {
            var currIndex = START_PAGE_INDEX
            var maxIndex = -1

            do
                emit(
                    either {
                        val episodeByCategoryResponse = client.executeAsync(
                            Request.Builder()
                                .get()
                                .url(constructUrl(searchQuery, currIndex))
                                .build()
                        )
                        currIndex++

                        val rawJson = !Either.fromNullable(episodeByCategoryResponse.body).mapLeft { ApiResponse.Failure.EmptyJson }
                        val searchResultEpisodes = !jsonEpisodeParser.parse(rawJson.string())

                        if (maxIndex == -1) {
                            maxIndex = searchResultEpisodes.meta.pages.total
                        }

                        ApiResponse.Success.Episodes(searchResultEpisodes.results)
                    }
                )
            while (currIndex < maxIndex + 1)
        }

    // Only add query parameters that differ from the defaults in order to limit the URL which is capped at max. 8192 characters
    private fun constructUrl(searchQuery: ElasticSearchRepo.SearchQuery, pageIndex: Int = START_PAGE_INDEX) =
        HttpUrl.Builder()
            .scheme("https")
            .host("vrtnu-api.vrt.be")
            .addEncodedPathSegment("search")
            .apply {
                if (searchQuery.index != DEFAULT_SEARCH_QUERY_INDEX) {
                    addQueryParameter("i", searchQuery.index.name.toLowerCase())
                }

                addQueryParameter("size", "${searchQuery.size}")

                if (searchQuery.order != DEFAULT_SEARCH_QUERY_ORDER) {
                    addQueryParameter("order", searchQuery.order.name.toLowerCase())
                }

                searchQuery.available?.let {
                    addQueryParameter("available", "$it")
                }
                searchQuery.query?.let {
                    addEncodedQueryParameter("q", it)
                }
                searchQuery.category?.let {
                    addEncodedQueryParameter("facets[categories]", it)
                }
                searchQuery.start?.let {
                    addQueryParameter("start", "$it")
                }
                searchQuery.end?.let {
                    addQueryParameter("end", "$it")
                }

                searchQuery.custom.forEach { (key, value) ->
                    addQueryParameter("facets[$key]", "[$value]")
                }

                if (pageIndex != START_PAGE_INDEX) {
                    addQueryParameter("from", "${((pageIndex - 1) * searchQuery.size) + 1}")
                }
            }
            .build()
}

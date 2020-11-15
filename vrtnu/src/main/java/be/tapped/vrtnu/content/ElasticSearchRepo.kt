package be.tapped.vrtnu.content

import arrow.core.Either
import arrow.core.EitherPartialOf
import arrow.core.Right
import arrow.core.computations.either
import arrow.typeclasses.suspended.BindSyntax
import be.tapped.vrtnu.content.ApiResponse.Failure.JsonParsingException
import be.tapped.vrtnu.content.ElasticSearchRepo.Companion.DEFAULT_SEARCH_QUERY_INDEX
import be.tapped.vrtnu.content.ElasticSearchRepo.Companion.DEFAULT_SEARCH_QUERY_ORDER
import be.tapped.vtmgo.common.executeAsync
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.time.seconds

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

        enum class Order(val queryParamName: String) {
            ASC("asc"),
            DESC("desc");
        }

        enum class Index(val queryParamName: String) {
            // VRT NU
            VIDEO("video"),

            // VRT
            CORPORATE("corporate")
        }
    }

    fun search(searchQuery: SearchQuery): Flow<Either<ApiResponse.Failure, ApiResponse.Success.Episodes>>
}

//fun <A, B> unfoldFlow(initial: A, next: suspend (A) -> Pair<A, B>?): Flow<B> =
//    flow {
//        var initial = initial
//        next(initial)?.let { (a, b) ->
//            initial = a
//            emit(b)
//        }
//    }

@JvmName("unfoldFlowEither")
fun <A, B, E> unfoldFlow(initial: A, next: suspend BindSyntax<EitherPartialOf<E>>.(A) -> Pair<A, B>?): Flow<Either<E, B>> =
    flow {
        var initial: A? = initial
        val res: Either<E, Unit> = either {
            do {
                val nextEither = next(this@either, initial!!)

                initial = if (nextEither != null) {
                    emit(Either.Right(nextEither.second))
                    nextEither.first
                } else null
            } while (initial != null)
        }
        when (res) {
            is Either.Left -> emit(res) // Emit failure
            is Either.Right -> Unit // Already emitted all values
        }
    }

internal class HttpElasticSearchRepo(
    private val client: OkHttpClient,
    private val jsonEpisodeParser: JsonEpisodeParser,
) : ElasticSearchRepo {

    companion object {
        private const val START_PAGE_INDEX = 1
    }

    override fun search(searchQuery: ElasticSearchRepo.SearchQuery): Flow<Either<ApiResponse.Failure, ApiResponse.Success.Episodes>> =
        unfoldFlow(START_PAGE_INDEX) { index ->
            val episodeByCategoryResponse = client.executeAsync(
                Request.Builder()
                    .get()
                    .url(constructUrl(searchQuery, index))
                    .build()
            )

            val rawJson = !Either.fromNullable(episodeByCategoryResponse.body).mapLeft { ApiResponse.Failure.EmptyJson }
            val searchResultEpisodes = !jsonEpisodeParser.parse(rawJson.string())

            if (index > searchResultEpisodes.meta.pages.total + 1) null
            else Pair(index + 1, ApiResponse.Success.Episodes(searchResultEpisodes.results))
        }

    // Only add query parameters that differ from the defaults in order to limit the URL which is capped at max. 8192 characters
    private fun constructUrl(searchQuery: ElasticSearchRepo.SearchQuery, pageIndex: Int = START_PAGE_INDEX) =
        HttpUrl.Builder()
            .scheme("https")
            .host("vrtnu-api.vrt.be")
            .addPathSegment("search")
            .apply {
                with(searchQuery) {
                    if (index != DEFAULT_SEARCH_QUERY_INDEX) {
                        addQueryParameter("i", index.queryParamName)
                    }

                    addQueryParameter("size", "$size")

                    if (order != DEFAULT_SEARCH_QUERY_ORDER) {
                        addQueryParameter("order", order.queryParamName)
                    }

                    available?.let {
                        addQueryParameter("available", "$it")
                    }
                    query?.let {
                        addEncodedQueryParameter("q", it)
                    }
                    category?.let {
                        addEncodedQueryParameter("facets[categories]", it)
                    }
                    start?.let {
                        addQueryParameter("start", "$it")
                    }
                    end?.let {
                        addQueryParameter("end", "$it")
                    }

                    custom.forEach { (key, value) ->
                        addQueryParameter("facets[$key]", "[$value]")
                    }

                    if (pageIndex != START_PAGE_INDEX) {
                        addQueryParameter("from", "${((pageIndex - 1) * size) + 1}")
                    }
                }
            }
            .build()
}

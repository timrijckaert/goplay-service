package be.tapped.vrtnu.content

import arrow.core.Either
import arrow.core.EitherPartialOf
import arrow.core.computations.either
import arrow.typeclasses.suspended.BindSyntax
import be.tapped.vrtnu.content.ApiResponse.Failure.JsonParsingException
import be.tapped.vrtnu.content.ElasticSearchQueryBuilder.applySearchQuery
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
    fun search(searchQuery: ElasticSearchQueryBuilder.SearchQuery): Flow<Either<ApiResponse.Failure, ApiResponse.Success.Episodes>>
}

internal class HttpElasticSearchRepo(
    private val client: OkHttpClient,
    private val jsonEpisodeParser: JsonEpisodeParser,
) : ElasticSearchRepo {

    override fun search(searchQuery: ElasticSearchQueryBuilder.SearchQuery): Flow<Either<ApiResponse.Failure, ApiResponse.Success.Episodes>> =
        unfoldFlow(searchQuery.pageIndex) { index ->
            val episodeByCategoryResponse = client.executeAsync(
                Request.Builder()
                    .get()
                    .url(constructUrl(searchQuery.copy(pageIndex = index)))
                    .build()
            )

            val rawJson = !Either.fromNullable(episodeByCategoryResponse.body).mapLeft { ApiResponse.Failure.EmptyJson }
            val searchResultEpisodes = !jsonEpisodeParser.parse(rawJson.string())

            if (index > searchResultEpisodes.meta.pages.total) null
            else Pair(index + 1, ApiResponse.Success.Episodes(searchResultEpisodes.results))
        }

    private fun constructUrl(searchQuery: ElasticSearchQueryBuilder.SearchQuery) =
        HttpUrl.Builder()
            .scheme("https")
            .host("vrtnu-api.vrt.be")
            .addPathSegment("search")
            .applySearchQuery(searchQuery)
            .build()

    private fun <A, B, E> unfoldFlow(initial: A, next: suspend BindSyntax<EitherPartialOf<E>>.(A) -> Pair<A, B>?): Flow<Either<E, B>> =
        flow {
            var initial: A? = initial
            val res: Either<E, Unit> = either {
                do {
                    val nextEither: Pair<A, B>? = next(this@either, initial!!)

                    initial = if (nextEither != null) {
                        emit(Either.Right(nextEither.second))
                        nextEither.first
                    } else null
                } while (initial != null)
            }
            when (res) {
                is Either.Left -> emit(res)
                is Either.Right -> Unit
            }
        }
}

object ElasticSearchQueryBuilder {

    private const val DEFAULT_SEARCH_SIZE = 150
    private const val MAX_SEARCH_SIZE = 300

    private const val DEFAULT_START_PAGE_INDEX = 1
    private val DEFAULT_SEARCH_QUERY_INDEX = SearchQuery.Index.VIDEO
    private val DEFAULT_SEARCH_QUERY_ORDER = SearchQuery.Order.DESC
    private const val DEFAULT_TRANSCODING_STATUS = "AVAILABLE"

    // https://github.com/add-ons/plugin.video.vrt.nu/wiki/VRT-NU-API#vrt-api-parameters
    data class SearchQuery(
        val size: Int = DEFAULT_SEARCH_SIZE,
        val index: Index = DEFAULT_SEARCH_QUERY_INDEX,
        val order: Order = DEFAULT_SEARCH_QUERY_ORDER,
        //TODO Can we convert this to an enum? What are the other values?
        val transcodingStatus: String = DEFAULT_TRANSCODING_STATUS,
        val pageIndex: Int = DEFAULT_SEARCH_SIZE,
        val available: Boolean? = null,
        val query: String? = null,
        val category: String? = null,
        val start: Long? = null,
        val end: Long? = null,
        val programName: String? = null,
        val custom: Map<String, String> = emptyMap(),
    ) {

        val from: Int
            get() = ((pageIndex - 1) * size) + 1

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

    private val nonWordCharacterRegex = Regex("\\W")
    private fun sanitizeProgramName(programName: String): String = programName.replace(nonWordCharacterRegex, "-").toLowerCase()

    // Only add query parameters that differ from the defaults in order to limit the URL which is capped at max. 8192 characters
    fun HttpUrl.Builder.applySearchQuery(searchQuery: SearchQuery): HttpUrl.Builder {
        return apply {
            with(searchQuery) {
                if (index != DEFAULT_SEARCH_QUERY_INDEX) {
                    addQueryParameter("i", index.queryParamName)
                }

                addQueryParameter("size", "$size")

                if (order != DEFAULT_SEARCH_QUERY_ORDER) {
                    addQueryParameter("order", order.queryParamName)
                }

                addQueryParameter("facets[transcodingStatus]", transcodingStatus)

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
                    // Supports dotted JSON Path notation
                    addQueryParameter("facets[$key]", "[$value]")
                }

                programName?.let {
                    addQueryParameter("facets[programName]", sanitizeProgramName(it))
                }

                if (pageIndex != DEFAULT_START_PAGE_INDEX) {
                    addQueryParameter("from", "$from")
                }
            }
        }
    }
}

package be.tapped.vrtnu.content

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.Validated
import arrow.core.ValidatedNel
import arrow.core.extensions.nonemptylist.semigroup.semigroup
import arrow.core.invalidNel
import arrow.core.validNel
import be.tapped.vrtnu.ApiResponse
import be.tapped.vrtnu.ApiResponse.Failure.Content.SearchQuery
import okhttp3.HttpUrl

public object ElasticSearchQueryBuilder {

    private const val DEFAULT_SEARCH_SIZE = 150
    private const val MAX_SEARCH_SIZE = 300
    private const val MAX_RESULT_WINDOW = 10_000

    private const val DEFAULT_START_PAGE_INDEX = 1
    private val DEFAULT_SEARCH_QUERY_INDEX = SearchQuery.Index.VIDEO
    private val DEFAULT_SEARCH_QUERY_ORDER = SearchQuery.Order.DESC
    private val DEFAULT_TRANSCODING_STATUS = TranscodingStatus.AVAILABLE

    // https://github.com/add-ons/plugin.video.vrt.nu/wiki/VRT-NU-API#vrt-api-parameters
    public data class SearchQuery(
        val size: Int = DEFAULT_SEARCH_SIZE,
        val index: Index = DEFAULT_SEARCH_QUERY_INDEX,
        val order: Order = DEFAULT_SEARCH_QUERY_ORDER,
        val transcodingStatus: TranscodingStatus = DEFAULT_TRANSCODING_STATUS,
        val pageIndex: Int = DEFAULT_START_PAGE_INDEX,
        val available: Boolean? = null,
        val query: String? = null,
        val category: String? = null,
        val start: Long? = null,
        val end: Long? = null,
        val programName: String? = null,
        val programUrl: String? = null,
        val whatsonId: String? = null,
        val custom: Map<String, String> = emptyMap(),
    ) {

        val from: Int
            get() = ((pageIndex - 1) * size) + 1

        public enum class Order(public val queryParamName: String) {
            ASC("asc"),
            DESC("desc");
        }

        public enum class Index(public val queryParamName: String) {
            // VRT NU
            VIDEO("video"),

            // VRT
            CORPORATE("corporate")
        }

        private fun validateResultWindow(): ValidatedNel<String, Unit> {
            val resultWindow = from + size
            return when (resultWindow > MAX_RESULT_WINDOW) {
                true -> "Result window is too large, from + size must be less than or equal to: $MAX_RESULT_WINDOW but was $resultWindow".invalidNel()
                else -> Unit.validNel()
            }
        }

        private fun validateMaxSearchSize(): ValidatedNel<String, Unit> =
            when (size > MAX_SEARCH_SIZE) {
                true -> "Search size can not be bigger than: $MAX_SEARCH_SIZE but was $size".invalidNel()
                false -> Unit.validNel()
            }

        internal fun validate(): Validated<NonEmptyList<String>, SearchQuery> =
            Validated.mapN(
                NonEmptyList.semigroup(),
                validateMaxSearchSize(),
                validateResultWindow()
            ) { _, _ -> this }
    }

    private val nonWordCharacterRegex = Regex("\\W")
    private fun sanitizeProgramName(programName: String): String = programName.replace(nonWordCharacterRegex, "-").toLowerCase()

    // Only add query parameters that differ from the defaults in order to limit the URL which is capped at max. 8192 characters
    public fun HttpUrl.Builder.applySearchQuery(searchQuery: SearchQuery): Either<ApiResponse.Failure.Content.SearchQuery, HttpUrl.Builder> {
        return searchQuery
            .validate().toEither()
            .mapLeft(::SearchQuery)
            .map {
                apply {
                    with(searchQuery) {
                        if (index != DEFAULT_SEARCH_QUERY_INDEX) {
                            addQueryParameter("i", index.queryParamName)
                        }

                        addQueryParameter("size", "$size")

                        if (order != DEFAULT_SEARCH_QUERY_ORDER) {
                            addQueryParameter("order", order.queryParamName)
                        }

                        addQueryParameter("facets[transcodingStatus]", transcodingStatus.name)

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

                        programUrl?.let {
                            addEncodedQueryParameter("facets[programUrl]", it)
                        }

                        whatsonId?.let {
                            addQueryParameter("facets[whatsonId]", it)
                        }

                        if (pageIndex != DEFAULT_START_PAGE_INDEX) {
                            addQueryParameter("from", "$from")
                        }
                    }
                }
            }
    }
}

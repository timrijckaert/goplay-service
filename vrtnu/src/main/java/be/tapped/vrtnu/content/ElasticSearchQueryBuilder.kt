package be.tapped.vrtnu.content

import okhttp3.HttpUrl

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
        val pageIndex: Int = DEFAULT_START_PAGE_INDEX,
        val available: Boolean? = null,
        val query: String? = null,
        val category: String? = null,
        val start: Long? = null,
        val end: Long? = null,
        val programName: String? = null,
        val programUrl: String? = null,
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

                programUrl?.let {
                    addEncodedQueryParameter("facets[programUrl]", it)
                }

                if (pageIndex != DEFAULT_START_PAGE_INDEX) {
                    addQueryParameter("from", "$from")
                }
            }
        }
    }
}

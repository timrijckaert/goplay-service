package be.tapped.goplay.content

import arrow.core.Either
import arrow.core.computations.either
import be.tapped.common.internal.executeAsync
import be.tapped.goplay.ApiResponse
import be.tapped.goplay.common.safeBodyString
import be.tapped.goplay.common.vierVijfZesApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

internal class JsonSearchResultsParser {
    fun parse(json: String): Either<ApiResponse.Failure, List<SearchHit>> = Either.catch {
        val hitsArr = Json.decodeFromString<JsonObject>(json)["hits"]!!.jsonObject["hits"]!!.jsonArray
        Json.decodeFromJsonElement<List<SearchHit>>(hitsArr)
    }.mapLeft(ApiResponse.Failure::JsonParsingException)
}

public sealed interface SearchRepo {
    public suspend fun search(query: String): Either<ApiResponse.Failure, ApiResponse.Success.Content.SearchResults>
}

internal class HttpSearchRepo(
    private val client: OkHttpClient,
    private val jsonSearchResultsParser: JsonSearchResultsParser,
) : SearchRepo {

    // curl -X POST \
    // -H  -d '{ "query": <query>,"sites":["vier", "vijf", "zes"],"page":0,"mode":"byDate"}' "https://api.viervijfzes.be/search"
    override suspend fun search(query: String): Either<ApiResponse.Failure, ApiResponse.Success.Content.SearchResults> = either {
        val searchResponse = client.executeAsync(
            Request.Builder().post(
                "${
                    buildJsonObject {
                        put("query", query)
                        put("sites", buildJsonArray {
                            add("vier")
                            add("vijf")
                            add("zes")
                        })
                        put("page", 0)
                        put("mode", "byDate")
                    }
                }".toRequestBody()
            ).url("$vierVijfZesApi/search").build()
        )

        val searchResults = jsonSearchResultsParser.parse(searchResponse.safeBodyString().bind()).bind()
        ApiResponse.Success.Content.SearchResults(searchResults)
    }
}

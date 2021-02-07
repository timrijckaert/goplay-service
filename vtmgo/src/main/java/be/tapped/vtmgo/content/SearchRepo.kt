package be.tapped.vtmgo.content

import arrow.core.Either
import arrow.core.computations.either
import be.tapped.common.internal.executeAsync
import be.tapped.vtmgo.ApiResponse
import be.tapped.vtmgo.ApiResponse.Failure.JsonParsingException
import be.tapped.vtmgo.common.HeaderBuilder
import be.tapped.vtmgo.common.safeBodyString
import be.tapped.vtmgo.profile.JWT
import be.tapped.vtmgo.profile.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import okhttp3.OkHttpClient
import okhttp3.Request

internal class JsonSearchResultResponseParser {
    fun parse(json: String): Either<ApiResponse.Failure, List<SearchResultResponse>> = Either.catch {
        Json.decodeFromJsonElement<List<SearchResultResponse>>(Json.decodeFromString<JsonObject>(json)["results"]!!.jsonArray)
    }.mapLeft(::JsonParsingException)
}

public sealed interface SearchRepo {
    public suspend fun search(jwt: JWT, profile: Profile, query: String): Either<ApiResponse.Failure, ApiResponse.Success.Content.Search>
}

internal class HttpSearchRepo(
        private val client: OkHttpClient,
        private val baseContentHttpUrlBuilder: BaseContentHttpUrlBuilder,
        private val headerBuilder: HeaderBuilder,
        private val jsonSearchResultResponseParser: JsonSearchResultResponseParser,
) : SearchRepo {

    override suspend fun search(jwt: JWT, profile: Profile, query: String): Either<ApiResponse.Failure, ApiResponse.Success.Content.Search> =
            withContext(Dispatchers.IO) {
                val searchResponse = client.executeAsync(
                        Request.Builder().get().headers(headerBuilder.authenticationHeaders(jwt, profile)).url(
                                baseContentHttpUrlBuilder.constructBaseContentUrl(profile.product).addPathSegment("search")
                                        .addEncodedQueryParameter("query", query).build()
                        ).build()
                )

                either {
                    ApiResponse.Success.Content.Search(!jsonSearchResultResponseParser.parse(!searchResponse.safeBodyString()))
                }
            }
}

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
import be.tapped.vtmgo.profile.VTMGOProduct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

internal class JsonChannelParser {
    internal fun parse(json: String): Either<ApiResponse.Failure, List<LiveChannel>> = Either.catch {
        val jsonObject = Json.decodeFromString<JsonObject>(json)
        Json.decodeFromJsonElement<List<LiveChannel>>(jsonObject["channels"]!!.jsonArray)
    }.mapLeft { JsonParsingException(it, json) }
}

public sealed interface ChannelRepo {
    public suspend fun fetchChannels(jwt: JWT, profile: Profile): Either<ApiResponse.Failure, ApiResponse.Success.Content.LiveChannels>
}

internal class HttpChannelRepo(
        private val client: OkHttpClient,
        private val baseContentHttpUrlBuilder: BaseContentHttpUrlBuilder,
        private val headerBuilder: HeaderBuilder,
        private val jsonChannelParser: JsonChannelParser,
) : ChannelRepo {

    override suspend fun fetchChannels(jwt: JWT, profile: Profile): Either<ApiResponse.Failure, ApiResponse.Success.Content.LiveChannels> =
            withContext(Dispatchers.IO) {
                val response = client.executeAsync(
                        Request
                                .Builder()
                                .headers(headerBuilder.authenticationHeaders(jwt, profile))
                                .get()
                                .url(constructUrl(profile.product))
                                .build()
                )

                either {
                    ApiResponse.Success.Content.LiveChannels(jsonChannelParser.parse(response.safeBodyString().bind()).bind())
                }
            }

    private fun constructUrl(product: VTMGOProduct): HttpUrl =
            baseContentHttpUrlBuilder.constructBaseContentUrl(product).addPathSegments("live").build()
}


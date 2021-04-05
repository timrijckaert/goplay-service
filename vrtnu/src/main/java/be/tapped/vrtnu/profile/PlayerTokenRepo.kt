package be.tapped.vrtnu.profile

import arrow.core.Either
import arrow.core.computations.either
import be.tapped.common.internal.executeAsync
import be.tapped.vrtnu.ApiResponse
import be.tapped.vrtnu.ApiResponse.Failure.JsonParsingException
import be.tapped.vrtnu.common.safeBodyString
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

public class JsonVRTPlayerTokenParser {
    public fun parse(json: String): Either<ApiResponse.Failure, VRTPlayerToken> =
            Either.catch { Json.decodeFromString<VRTPlayerToken>(json) }.mapLeft(::JsonParsingException)
}

public sealed interface PlayerTokenRepo {
    public suspend fun fetchVRTPlayerToken(xVRTToken: XVRTToken): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.PlayerToken>
}

internal class HttpPlayerTokenRepo(
        private val client: OkHttpClient,
        private val jsonVRTPlayerTokenParser: JsonVRTPlayerTokenParser,
) : PlayerTokenRepo {
    override suspend fun fetchVRTPlayerToken(xVRTToken: XVRTToken): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.PlayerToken> =
            with(Dispatchers.IO) {
                val vrtPlayerTokenResponse = client.executeAsync(
                        Request.Builder().header("Content-Type", "application/json")
                                .post("${buildJsonObject { put("identityToken", xVRTToken.token) }}".toRequestBody())
                                .url(
                                        HttpUrl.Builder()
                                                .scheme("https")
                                                .host("media-services-public.vrt.be")
                                                .addPathSegments("vualto-video-aggregator-web/rest/external/v1")
                                                .addPathSegment("tokens")
                                                .build()
                                ).build()
                )

                either {
                    ApiResponse.Success.Authentication.PlayerToken(jsonVRTPlayerTokenParser.parse(vrtPlayerTokenResponse.safeBodyString().bind()).bind())
                }
            }
}

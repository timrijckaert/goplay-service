package be.tapped.vrtnu.profile

import arrow.core.Either
import arrow.core.computations.either
import be.tapped.vrtnu.profile.ProfileResponse.Failure.JsonParsingException
import be.tapped.vtmgo.common.executeAsync
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class JsonVRTPlayerTokenParser {
    suspend fun parse(json: String): Either<ProfileResponse.Failure, VRTPlayerToken> =
        Either.catch { Json.decodeFromString<VRTPlayerToken>(json) }.mapLeft(::JsonParsingException)
}

interface PlayerTokenRepo {
    suspend fun fetchVRTPlayerToken(xVRTToken: XVRTToken): Either<ProfileResponse.Failure, ProfileResponse.Success.PlayerToken>
}

internal class HttpPlayerTokenRepo(
    private val client: OkHttpClient,
    private val jsonVRTPlayerTokenParser: JsonVRTPlayerTokenParser,
) : PlayerTokenRepo {
    override suspend fun fetchVRTPlayerToken(xVRTToken: XVRTToken): Either<ProfileResponse.Failure, ProfileResponse.Success.PlayerToken> =
        with(Dispatchers.IO) {
            val vrtPlayerTokenResponse = client.executeAsync(
                Request.Builder()
                    .header("Content-Type", "application/json")
                    .post(buildJsonObject { put("identityToken", xVRTToken.token) }.toString().toRequestBody())
                    .url(HttpUrl.Builder()
                        .scheme("https")
                        .host("media-services-public.vrt.be")
                        .addPathSegments("vualto-video-aggregator-web/rest/external/v1")
                        .addPathSegment("tokens")
                        .build()
                    )
                    .build()
            )

            either {
                val vrtPlayerTokenJson = !Either.fromNullable(vrtPlayerTokenResponse.body).mapLeft { ProfileResponse.Failure.EmptyJson }
                ProfileResponse.Success.PlayerToken(!jsonVRTPlayerTokenParser.parse(vrtPlayerTokenJson.string()))
            }
        }
}

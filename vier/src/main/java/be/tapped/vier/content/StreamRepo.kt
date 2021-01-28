package be.tapped.vier.content

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.flatMap
import be.tapped.common.internal.executeAsync
import be.tapped.vier.ApiResponse
import be.tapped.vier.common.safeBodyString
import be.tapped.vier.common.vierBaseApiUrl
import be.tapped.vier.profile.IdToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request

internal class JsonStreamParser {
    suspend fun parse(videoUuid: VideoUuid, json: String): Either<ApiResponse.Failure, M3U8Stream> =
        Either.catch { Json.decodeFromString<JsonObject>(json) }.mapLeft { ApiResponse.Failure.Stream.NoStreamFound(videoUuid) }.flatMap {
            Either.catch { M3U8Stream(it["video"]!!.jsonObject["S"]!!.jsonPrimitive.content) }
                .mapLeft { ApiResponse.Failure.JsonParsingException(it) }
        }
}

public interface StreamRepo {
    public suspend fun streamByVideoUuid(idToken: IdToken, videoUuid: VideoUuid): Either<ApiResponse.Failure, ApiResponse.Success.Stream>
}

internal class HttpStreamRepo(
    private val client: OkHttpClient,
    private val jsonStreamParser: JsonStreamParser,
) : StreamRepo {

    // curl -X GET \
    // -H "Authorization: <IdToken>" \
    // -H "https://api.viervijfzes.be/content/<VideoUuid>"
    override suspend fun streamByVideoUuid(idToken: IdToken, videoUuid: VideoUuid): Either<ApiResponse.Failure, ApiResponse.Success.Stream> =
        withContext(Dispatchers.IO) {
            either {
                val response = client.executeAsync(
                    Request.Builder().get().url("$vierBaseApiUrl/content/${videoUuid.id}").header("Authorization", idToken.token).build()
                )
                ApiResponse.Success.Stream(!jsonStreamParser.parse(videoUuid, !response.safeBodyString()))
            }
        }
}

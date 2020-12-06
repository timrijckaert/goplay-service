package be.tapped.vtmgo.content

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.right
import be.tapped.common.executeAsync
import be.tapped.common.validateResponse
import be.tapped.vtmgo.ApiResponse
import be.tapped.vtmgo.ApiResponse.Failure
import be.tapped.vtmgo.ApiResponse.Failure.Stream.*
import be.tapped.vtmgo.common.HeaderBuilder
import be.tapped.vtmgo.common.safeBodyString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import okhttp3.Headers
import okhttp3.Headers.Companion.headersOf
import okhttp3.Headers.Companion.toHeaders
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.intellij.lang.annotations.Language

internal class JsonStreamResponseParser {
    suspend fun parse(json: String): Either<Failure, StreamResponse> =
        Either.catch {
            Json.decodeFromJsonElement<StreamResponse>(Json.decodeFromString<JsonObject>(json)["video"]!!.jsonObject)
        }.mapLeft(Failure::JsonParsingException)
}

interface StreamRepo {
    suspend fun fetchStream(liveChannel: LiveChannel): Either<Failure, AnvatoStream>
}

internal class HttpStreamRepo(
    private val client: OkHttpClient,
    private val headerBuilder: HeaderBuilder,
    private val jsonStreamResponseParser: JsonStreamResponseParser,
    private val anvatoRepo: AnvatoRepo,
) : StreamRepo {

    companion object {
        private const val POPCORN_API_KEY = "zTxhgTEtb055Ihgw3tN158DZ0wbbaVO86lJJulMl"
    }

    override suspend fun fetchStream(liveChannel: LiveChannel): Either<Failure, AnvatoStream> =
        withContext(Dispatchers.IO) {
            val liveStreamResponse = client.executeAsync(
                Request.Builder()
                    .get()
                    .headers(constructHeaders())
                    .url(constructUrl("channels", liveChannel.channelId))
                    .build()
            )

            either {
                val streamInfo = !jsonStreamResponseParser.parse(!liveStreamResponse.safeBodyString())
                val anvato = !Either.fromNullable(streamInfo.streams.map(Stream::anvato).firstOrNull()).mapLeft { NoAnvatoStreamFound }
                !anvatoRepo.fetchStream(anvato, streamInfo)
            }
        }

    private fun constructHeaders(): Headers =
        Headers.Builder()
            .addAll(headerBuilder.defaultHeaders)
            .add("x-api-key", POPCORN_API_KEY)
            .add("Popcorn-SDK-Version", "2")
            //TODO check if this is needed since it returns the same response for the channels
            .add("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 6.0.1; Nexus 5 Build/M4B30Z)")
            .build()

    //TODO should be generic to function for all types
    private fun constructUrl(streamType: String, channelId: String): HttpUrl =
        HttpUrl.Builder()
            .scheme("https")
            .host("videoplayer-service.api.persgroep.cloud")
            .addPathSegment("config")
            .addPathSegment(streamType)
            .addQueryParameter("startPosition", "0.0")
            .addQueryParameter("autoPlay", "true")
            .addPathSegment(channelId)
            .build()

}

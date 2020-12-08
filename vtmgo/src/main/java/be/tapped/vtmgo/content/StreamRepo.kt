package be.tapped.vtmgo.content

import arrow.core.Either
import arrow.core.computations.either
import be.tapped.common.executeAsync
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
import kotlinx.serialization.json.jsonObject
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

internal class JsonLiveStreamResponseParser {
    suspend fun parse(json: String): Either<Failure, StreamResponse> =
        Either.catch {
            val videoObject = Json.decodeFromString<JsonObject>(json)["video"]!!.jsonObject
            Json.decodeFromJsonElement<StreamResponse>(videoObject)
        }.mapLeft(Failure::JsonParsingException)
}

interface StreamRepo {

    suspend fun fetchStream(liveChannel: LiveChannel): Either<Failure, ApiResponse.Success.Stream>

    suspend fun fetchStream(episodeId: String): Either<Failure, ApiResponse.Success.Stream>

}

internal class HttpStreamRepo(
    private val client: OkHttpClient,
    private val headerBuilder: HeaderBuilder,
    private val jsonLiveStreamResponseParser: JsonLiveStreamResponseParser,
    private val anvatoRepo: AnvatoRepo,
) : StreamRepo {

    companion object {
        private const val POPCORN_API_KEY = "zTxhgTEtb055Ihgw3tN158DZ0wbbaVO86lJJulMl"
    }

    override suspend fun fetchStream(liveChannel: LiveChannel): Either<Failure, ApiResponse.Success.Stream> =
        either {
            val streamInfo = !getStreamResponseForId("channels", liveChannel.channelId)
            val anvato = !anvatoFromStreamInfo(streamInfo)
            ApiResponse.Success.Stream(!anvatoRepo.fetchLiveStream(anvato, streamInfo))
        }

    override suspend fun fetchStream(episodeId: String): Either<Failure, ApiResponse.Success.Stream> =
        either {
            val streamInfo = !getStreamResponseForId("episodes", episodeId)
            val anvato = !anvatoFromStreamInfo(streamInfo)
            ApiResponse.Success.Stream(!anvatoRepo.fetchEpisodeStream(anvato, streamInfo))
        }

    private fun anvatoFromStreamInfo(streamInfo: StreamResponse) =
        Either.fromNullable(streamInfo.streams.map(Stream::anvato).firstOrNull()).mapLeft { NoAnvatoStreamFound }

    private suspend fun getStreamResponseForId(streamType: String, id: String): Either<Failure, StreamResponse> =
        withContext(Dispatchers.IO) {
            val response = client.executeAsync(
                Request.Builder()
                    .get()
                    .headers(
                        Headers.Builder()
                            .addAll(headerBuilder.defaultHeaders)
                            .add("x-api-key", POPCORN_API_KEY)
                            .add("Popcorn-SDK-Version", "2")
                            //TODO check if this is needed since it returns the same response for the channels
                            .add("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 6.0.1; Nexus 5 Build/M4B30Z)")
                            .build()
                    )
                    .url(
                        HttpUrl.Builder()
                            .scheme("https")
                            .host("videoplayer-service.api.persgroep.cloud")
                            .addPathSegments("config/$streamType")
                            .addQueryParameter("startPosition", "0.0")
                            .addQueryParameter("autoPlay", "true")
                            .addPathSegment(id)
                            .build()
                    )
                    .build()
            )

            either { !jsonLiveStreamResponseParser.parse(!response.safeBodyString()) }
        }

}

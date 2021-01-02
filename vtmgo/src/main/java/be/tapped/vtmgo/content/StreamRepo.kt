package be.tapped.vtmgo.content

import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.Validated
import arrow.core.computations.either
import arrow.core.flatMap
import arrow.core.invalid
import arrow.core.toT
import arrow.core.valid
import be.tapped.common.internal.executeAsync
import be.tapped.vtmgo.ApiResponse
import be.tapped.vtmgo.ApiResponse.Failure
import be.tapped.vtmgo.ApiResponse.Failure.Stream.UnsupportedTargetType
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
    fun parse(json: String): Either<Failure, StreamResponse> =
        Either.catch {
            val videoObject = Json.decodeFromString<JsonObject>(json)["video"]!!.jsonObject
            Json.decodeFromJsonElement<StreamResponse>(videoObject)
        }.mapLeft(Failure::JsonParsingException)
}

internal class StreamResponseParser(
    private val dashStreamParser: DashStreamParser,
    private val hlsStreamParser: HlsStreamParser,
    private val anvatoStreamParser: AnvatoStreamParser,
) {

    private fun rawStreamTypeFromStreamType(streamType: StreamType) =
        when (streamType) {
            StreamType.ANVATO_LIVE,
            StreamType.ANVATO_VOD,
            -> "anvato"
            StreamType.DASH -> "dash"
            StreamType.HLS -> "hls"
        }

    suspend fun streamForStreamType(
        streamType: StreamType,
        streamResponse: StreamResponse,
    ): Either<Failure.Stream, ApiResponse.Success.Stream> =
        either {
            val rawStreamType = rawStreamTypeFromStreamType(streamType)
            val rawStream = !Either.fromNullable(streamResponse.streams.firstOrNull { it.type == rawStreamType })
                .mapLeft { Failure.Stream.NoStreamFoundForType(rawStreamType) }
            !when (streamType) {
                StreamType.ANVATO_LIVE -> anvatoStreamParser.fetchLiveStream(rawStream, streamResponse)
                StreamType.ANVATO_VOD -> anvatoStreamParser.fetchEpisodeStream(rawStream, streamResponse)
                StreamType.DASH -> dashStreamParser.parse(rawStream, streamResponse.subtitles)
                StreamType.HLS -> hlsStreamParser.parse(rawStream, streamResponse.subtitles)
            }
        }
}

internal class DashStreamParser {
    fun parse(
        dashStream: Stream,
        subtitles: List<Subtitle>,
    ): Either<Failure.Stream.NoDashStreamNotFound, ApiResponse.Success.Stream.Dash> =
        Either.catch {
            ApiResponse.Success.Stream.Dash(
                MPDUrl(dashStream.url!!),
                LicenseUrl(dashStream.drm!!.licenseUrl),
                subtitles
            )
        }.mapLeft { Failure.Stream.NoDashStreamNotFound }
}

internal class HlsStreamParser {
    fun parse(hlsStream: Stream, subtitles: List<Subtitle>): Either<Failure.Stream.NoHlsStreamNotFound, ApiResponse.Success.Stream.Hls> =
        Either.catch {
            ApiResponse.Success.Stream.Hls(
                HlsUrl(hlsStream.url!!),
                LicenseUrl(hlsStream.drm!!.licenseUrl),
                HlsCertificate(hlsStream.drm.certificate!!),
                subtitles
            )
        }.mapLeft { Failure.Stream.NoHlsStreamNotFound }
}

internal class AnvatoStreamParser(private val anvatoRepo: AnvatoRepo) {
    private suspend fun fetch(stream: Stream, f: suspend AnvatoRepo.(Anvato) -> Either<Failure, AnvatoStream>) =
        Either.fromNullable(stream.anvato)
            .flatMap { anvatoRepo.f(it) }
            .map(ApiResponse.Success.Stream::Anvato)
            .mapLeft { Failure.Stream.NoAnvatoStreamNotFound }

    suspend fun fetchLiveStream(
        stream: Stream,
        streamResponse: StreamResponse,
    ): Either<Failure.Stream.NoAnvatoStreamNotFound, ApiResponse.Success.Stream.Anvato> =
        fetch(stream) { anvatoRepo.fetchLiveStream(it, streamResponse) }

    suspend fun fetchEpisodeStream(
        stream: Stream,
        streamResponse: StreamResponse,
    ): Either<Failure.Stream.NoAnvatoStreamNotFound, ApiResponse.Success.Stream.Anvato> =
        fetch(stream) { anvatoRepo.fetchEpisodeStream(it, streamResponse) }
}

public interface StreamRepo {

    public suspend fun fetchStream(liveChannel: LiveChannel): Either<Failure, ApiResponse.Success.Stream.Anvato>

    public suspend fun fetchStream(
        target: TargetResponse.Target,
        streamType: StreamType = StreamType.DASH,
    ): Either<Failure, ApiResponse.Success.Stream>

}

internal class HttpStreamRepo(
    private val client: OkHttpClient,
    private val headerBuilder: HeaderBuilder,
    private val jsonLiveStreamResponseParser: JsonLiveStreamResponseParser,
    private val streamResponseParser: StreamResponseParser,
) : StreamRepo {

    companion object {
        private const val POPCORN_API_KEY = "jL3yNhGpDsaew9CqJrDPq2UzMrlmNVbnadUXVOET"
        private const val POPCORN_SDK_VERSION = "4"
    }

    override suspend fun fetchStream(liveChannel: LiveChannel): Either<Failure, ApiResponse.Success.Stream.Anvato> =
        either {
            val streamResponse: StreamResponse = !getStreamResponseForId("channels", liveChannel.channelId)
            !streamResponseParser.streamForStreamType(StreamType.ANVATO_LIVE, streamResponse).map { it as ApiResponse.Success.Stream.Anvato }
        }

    override suspend fun fetchStream(target: TargetResponse.Target, streamType: StreamType): Either<Failure, ApiResponse.Success.Stream> {
        fun canFetchResults(target: TargetResponse.Target): Validated<UnsupportedTargetType, Tuple2<String, String>> =
            when (target) {
                is TargetResponse.Target.Movie -> ("movies" toT target.id).valid()
                is TargetResponse.Target.Episode -> ("episodes" toT target.id).valid()
                is TargetResponse.Target.Program,
                is TargetResponse.Target.External,
                -> UnsupportedTargetType(target).invalid()
            }

        return either {
            val (pathSegmentForStreamType, id) = !canFetchResults(target).toEither()
            val streamResponse = !getStreamResponseForId(pathSegmentForStreamType, id)
            !streamResponseParser.streamForStreamType(streamType, streamResponse)
        }
    }

    private suspend fun getStreamResponseForId(pathSegmentForStreamType: String, id: String): Either<Failure, StreamResponse> =
        withContext(Dispatchers.IO) {
            val response = client.executeAsync(
                Request.Builder()
                    .get()
                    .headers(
                        Headers.Builder()
                            .addAll(headerBuilder.defaultHeaders)
                            .add("x-api-key", POPCORN_API_KEY)
                            .add("Popcorn-SDK-Version", POPCORN_SDK_VERSION)
                            .build()
                    )
                    .url(
                        HttpUrl.Builder()
                            .scheme("https")
                            .host("videoplayer-service.api.persgroep.cloud")
                            .addPathSegments("config/$pathSegmentForStreamType")
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

package be.tapped.vrtnu.content

import arrow.core.Either
import arrow.core.computations.either
import be.tapped.common.internal.executeAsync
import be.tapped.vrtnu.ApiResponse.Failure
import be.tapped.vrtnu.ApiResponse.Failure.JsonParsingException
import be.tapped.vrtnu.ApiResponse.Success
import be.tapped.vrtnu.common.safeBodyString
import be.tapped.vrtnu.profile.VRTPlayerToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

public class JsonStreamInformationParser {
    public fun parse(json: String): Either<Failure, StreamInformation> =
            Either
                    .catch { Json.decodeFromString<StreamInformation>(json) }
                    .mapLeft(::JsonParsingException)
}

public sealed interface StreamRepo {

    public suspend fun getVODStream(
            vrtPlayerToken: VRTPlayerToken, videoId: VideoId, publicationId: PublicationId
    ): Either<Failure, Success.Content.StreamInfo>

    public suspend fun getLiveStream(
            vrtPlayerToken: VRTPlayerToken, videoId: VideoId
    ): Either<Failure, Success.Content.StreamInfo>
}

public class HttpStreamRepo(
        private val client: OkHttpClient,
        private val jsonStreamInformationParser: JsonStreamInformationParser,
) : StreamRepo {

    public companion object {
        private const val CLIENT = "vrtvideo@PROD"
    }

    private fun constructVideoStreamUrl(
            vrtPlayerToken: VRTPlayerToken, videoId: VideoId, publicationId: PublicationId?
    ): HttpUrl =
            HttpUrl.Builder()
                    .scheme("https")
                    .host("media-services-public.vrt.be")
                    .addPathSegments("vualto-video-aggregator-web/rest/external/v1")
                    .addPathSegment("videos").apply {
                        val vId = videoId.id
                        val pId = publicationId?.id
                        pId?.let {
                            addEncodedPathSegment("${pId}$${vId}")
                        } ?: addEncodedPathSegment(vId)
                    }
                    .addQueryParameter("vrtPlayerToken", vrtPlayerToken.vrtPlayerToken)
                    .addQueryParameter("client", CLIENT).build()

    override suspend fun getLiveStream(
            vrtPlayerToken: VRTPlayerToken, videoId: VideoId
    ): Either<Failure, Success.Content.StreamInfo> =
            getStream(constructVideoStreamUrl(vrtPlayerToken, videoId, null))

    override suspend fun getVODStream(
            vrtPlayerToken: VRTPlayerToken,
            videoId: VideoId,
            publicationId: PublicationId,
    ): Either<Failure, Success.Content.StreamInfo> =
            getStream(constructVideoStreamUrl(vrtPlayerToken, videoId, publicationId))

    private suspend fun getStream(httpUrl: HttpUrl): Either<Failure, Success.Content.StreamInfo> =
            withContext(Dispatchers.IO) {
                val videoStreamResponse = client.executeAsync(Request.Builder().get().url(httpUrl).build())

                either {
                    Success.Content.StreamInfo(!jsonStreamInformationParser.parse(!videoStreamResponse.safeBodyString()))
                }
            }

}

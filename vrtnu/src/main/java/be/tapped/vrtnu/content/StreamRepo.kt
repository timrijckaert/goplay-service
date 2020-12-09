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
    public suspend fun parse(json: String): Either<Failure, StreamInformation> =
        Either.catch { Json.decodeFromString<StreamInformation>(json) }.mapLeft(::JsonParsingException)
}

public interface StreamRepo {

    /***
     * Get stream information for a videoId and optional publication id.
     * When a video is a live stream no publication id is required.
     */
    public suspend fun getStream(
        vrtPlayerToken: VRTPlayerToken,
        videoId: String,
        publicationId: String? = null,
    ): Either<Failure, Success.Content.StreamInfo>

    //TODO("This will need web scraping")
    //suspend fun getStreamByUrl(
    //    vrtPlayerToken: VRTPlayerToken,
    //    videoId: String?,
    //    url: String,
    //): Either<Failure, Success.Content.StreamInfo>

}

public class HttpStreamRepo(
    private val client: OkHttpClient,
    private val jsonStreamInformationParser: JsonStreamInformationParser,
) : StreamRepo {

    public companion object {
        private const val CLIENT = "vrtvideo@PROD"
    }

    private fun constructVideoStreamUrl(vrtPlayerToken: VRTPlayerToken, videoId: String, publicationId: String? = null): HttpUrl =
        HttpUrl.Builder()
            .scheme("https")
            .host("media-services-public.vrt.be")
            .addPathSegments("vualto-video-aggregator-web/rest/external/v1")
            .addPathSegment("videos")
            .apply {
                publicationId?.let {
                    addEncodedPathSegment("${publicationId}$${videoId}")
                } ?: addEncodedPathSegment(videoId)
            }
            .addQueryParameter("vrtPlayerToken", vrtPlayerToken.vrtPlayerToken)
            .addQueryParameter("client", CLIENT)
            .build()

    override suspend fun getStream(
        vrtPlayerToken: VRTPlayerToken,
        videoId: String,
        publicationId: String?,
    ): Either<Failure, Success.Content.StreamInfo> =
        withContext(Dispatchers.IO) {
            val videoStreamResponse = client.executeAsync(
                Request.Builder()
                    .get()
                    .url(constructVideoStreamUrl(vrtPlayerToken, videoId, publicationId))
                    .build()
            )

            either {
                Success.Content.StreamInfo(!jsonStreamInformationParser.parse(!videoStreamResponse.safeBodyString()))
            }
        }

}

package be.tapped.vrtnu.content

import arrow.core.Either
import arrow.core.computations.either
import be.tapped.vrtnu.content.ApiResponse.Failure
import be.tapped.vrtnu.content.ApiResponse.Failure.EmptyJson
import be.tapped.vrtnu.content.ApiResponse.Failure.JsonParsingException
import be.tapped.vrtnu.content.ApiResponse.Success
import be.tapped.vrtnu.profile.VRTPlayerToken
import be.tapped.vtmgo.common.executeAsync
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

class JsonStreamInformationParser {
    suspend fun parse(json: String): Either<Failure, StreamInformation> =
        Either.catch { Json.decodeFromString<StreamInformation>(json) }.mapLeft(::JsonParsingException)
}

interface StreamRepo {

    /***
     * Get stream information for a videoId and optional publication id.
     * When a video is a live stream no publication id is required.
     */
    suspend fun getStream(
        vrtPlayerToken: VRTPlayerToken,
        videoId: String,
        publicationId: String? = null,
    ): Either<Failure, Success.StreamInfo>

    suspend fun getStreamByUrl(
        vrtPlayerToken: VRTPlayerToken,
        videoId: String?,
        url: String,
    ): Either<Failure, Success.StreamInfo>

}

class HttpStreamRepo(
    private val client: OkHttpClient,
    private val jsonStreamInformationParser: JsonStreamInformationParser,
) : StreamRepo {

    companion object {
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
    ): Either<Failure, Success.StreamInfo> =
        withContext(Dispatchers.IO) {
            val videoStreamResponse = client.executeAsync(
                Request.Builder()
                    .get()
                    .url(constructVideoStreamUrl(vrtPlayerToken, videoId, publicationId))
                    .build()
            ).body

            either {
                val json = !Either.fromNullable(videoStreamResponse).mapLeft { EmptyJson }
                Success.StreamInfo(!jsonStreamInformationParser.parse(json.string()))
            }
        }

    override suspend fun getStreamByUrl(
        vrtPlayerToken: VRTPlayerToken,
        videoId: String?,
        url: String,
    ): Either<Failure, Success.StreamInfo> {
        TODO("This will need web scraping")
    }

}

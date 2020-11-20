package be.tapped.vrtnu.content

import arrow.core.Either
import arrow.core.computations.either
import be.tapped.vrtnu.authentication.VRTPlayerToken
import be.tapped.vrtnu.content.ApiResponse.*
import be.tapped.vrtnu.content.ApiResponse.Failure.*
import be.tapped.vtmgo.common.executeAsync
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

    suspend fun getVODStream(
        vrtPlayerToken: VRTPlayerToken,
        videoId: String,
        publicationId: String,
    ): Either<Failure, Success.StreamInfo>

    suspend fun getLiveStream(vrtPlayerToken: VRTPlayerToken, videoId: String): Either<Failure, Success.StreamInfo>

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

    private fun constructVideoStreamUrl(publicationId: String, videoId: String, vrtPlayerToken: VRTPlayerToken): HttpUrl =
        HttpUrl.Builder()
            .scheme("https")
            .host("media-services-public.vrt.be")
            .addPathSegments("vualto-video-aggregator-web/rest/external/v1")
            .addPathSegment("videos")
            .addEncodedPathSegment("${publicationId}$${videoId}")
            .addQueryParameter("vrtPlayerToken", vrtPlayerToken.vrtPlayerToken)
            .addQueryParameter("client", CLIENT)
            .build()

    override suspend fun getVODStream(
        vrtPlayerToken: VRTPlayerToken,
        videoId: String,
        publicationId: String,
    ): Either<Failure, Success.StreamInfo> {
        val videoStreamResponse = client.executeAsync(
            Request.Builder()
                .get()
                .url(constructVideoStreamUrl(publicationId, videoId, vrtPlayerToken))
                .build()
        ).body

        return either {
            val json = !Either.fromNullable(videoStreamResponse).mapLeft { EmptyJson }
            Success.StreamInfo(!jsonStreamInformationParser.parse(json.string()))
        }
    }

    override suspend fun getLiveStream(vrtPlayerToken: VRTPlayerToken, videoId: String): Either<Failure, Success.StreamInfo> {
        TODO("Not yet implemented")
    }

    override suspend fun getStreamByUrl(
        vrtPlayerToken: VRTPlayerToken,
        videoId: String?,
        url: String,
    ): Either<Failure, Success.StreamInfo> {
        TODO("This will need web scraping")
    }

}

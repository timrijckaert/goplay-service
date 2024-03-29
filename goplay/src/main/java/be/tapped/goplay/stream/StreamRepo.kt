package be.tapped.goplay.stream

import arrow.core.Either
import arrow.core.Either.Companion.catch
import arrow.core.computations.either
import arrow.core.left
import be.tapped.goplay.Failure
import be.tapped.goplay.Stream
import be.tapped.goplay.apiGoPlay
import be.tapped.goplay.apiVierVijfZes
import be.tapped.goplay.content.Program
import be.tapped.goplay.profile.IdToken
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.apache.http.HttpHeaders

internal fun interface StreamRepo {
    suspend fun streamByVideoUuid(videoId: Program.Detail.Playlist.Episode.VideoUuid, idToken: IdToken): Either<Failure, Stream>
}

internal fun httpStreamRepo(client: HttpClient, mpegDashStreamResolver: MpegDashStreamResolver, hlsStreamResolver: HLSStreamResolver): StreamRepo =
    StreamRepo { videoId, idToken ->
        withContext(Dispatchers.IO) {
            either {
                val videoResponse = client.safeGet<JsonElement>(idToken, "$apiVierVijfZes/content/${videoId.videoUuid}").bind()
                if (videoResponse is JsonObject) {
                    val videoObj = videoResponse.jsonObject
                    when {
                        videoObj.containsKey("videoDash") -> mpegDashStreamResolver.fetchMpegDashStream(videoId, videoObj, idToken)
                        videoObj.containsKey("video") -> hlsStreamResolver.fetchHlsStream(videoId, videoObj)
                        else -> Failure.Stream.UnknownStream(videoId, videoObj).left()
                    }.bind()
                } else {
                    Stream(ResolvedStream.NoStreamFound(videoId))
                }
            }
        }
    }

//<editor-fold desc="MPEG-DASH">
internal fun interface MpegDashStreamResolver {
    suspend fun fetchMpegDashStream(
        videoId: Program.Detail.Playlist.Episode.VideoUuid,
        videoObj: JsonObject,
        idToken: IdToken
    ): Either<Failure, Stream>
}

/**
 * Example of MPEG dash response
 *
 * ```json
 * {
 *   "drmKey": {
 *     "S": "very-secret-drm-key"
 *   },
 *   "videoDash": {
 *     "S": "https://www.mpg-dash.be"
 *   },
 *   "video": {
 *     "S": "https://www.hls-video.be"
 *   }
 * }
 * ```
 *
 * Example of DRM response
 *
 * ```json
 * {
 *   "auth": "secret-authentication-token"
 * }
 * ```
 */
internal fun mpegDashStreamResolver(client: HttpClient): MpegDashStreamResolver =
    MpegDashStreamResolver { videoId, videoObj, idToken ->
        withContext(Dispatchers.IO) {
            either {
                val mpegDash =
                    catch {
                        val drmKey = videoObj.getValue("drmKey").jsonObject.getValue("S").jsonPrimitive.content
                        val drmResponseJson = client.safeGet<JsonObject>(idToken, "$apiGoPlay/video/xml/${drmKey}").bind()
                        val auth = catch(drmResponseJson.getValue("auth").jsonPrimitive::content).mapLeft { Failure.Stream.DrmAuth(videoId, drmResponseJson, it) }.bind()
                        ResolvedStream.MpegDash(
                            videoId,
                            videoObj.getValue("videoDash").jsonObject.getValue("S").jsonPrimitive.content,
                            auth
                        )
                    }.mapLeft { Failure.Stream.MpegDash(videoId, videoObj, it) }.bind()
                Stream(mpegDash)
            }
        }
    }
//</editor-fold>

//<editor-fold desc="HLS">
internal fun interface HLSStreamResolver {
    suspend fun fetchHlsStream(
        videoId: Program.Detail.Playlist.Episode.VideoUuid,
        videoObj: JsonObject,
    ): Either<Failure, Stream>
}

/**
 * Example of HLS response
 *
 * ```json
 * {
 *   "video": {
 *     "S": "https://www.hls-video.be"
 *   }
 * }
 * ```
 */
internal fun hlsStreamResolver(): HLSStreamResolver =
    HLSStreamResolver { videoId, videoObj ->
        withContext(Dispatchers.IO) {
            either {
                val hlsStream =
                    catch {
                        ResolvedStream.Hls(
                            videoId,
                            videoObj.getValue("video").jsonObject.getValue("S").jsonPrimitive.content
                        )
                    }.mapLeft { Failure.Stream.Hls(videoId, videoObj, it) }.bind()
                Stream(hlsStream)
            }
        }
    }
//</editor-fold>

public suspend inline fun <reified T> HttpClient.safeGet(idToken: IdToken, urlString: String): Either<Failure.Network, T> =
    catch {
        get<T>(urlString) {
            headers { append(HttpHeaders.AUTHORIZATION, idToken.token) }
        }
    }.mapLeft(Failure::Network)

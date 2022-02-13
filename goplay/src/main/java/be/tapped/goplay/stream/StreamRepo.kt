package be.tapped.goplay.stream

import arrow.core.Either
import arrow.core.Either.Companion.catch
import arrow.core.computations.either
import arrow.core.left
import be.tapped.goplay.ApiResponse
import be.tapped.goplay.apiGoPlay
import be.tapped.goplay.apiVierVijfZes
import be.tapped.goplay.content.Program
import be.tapped.goplay.profile.IdToken
import be.tapped.goplay.safeGet
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.apache.http.HttpHeaders

internal fun interface StreamRepo {
    suspend fun streamByVideoUuid(videoId: Program.Detail.Playlist.Episode.VideoUuid, idToken: IdToken): Either<ApiResponse.Failure, ApiResponse.Success.Stream>
}

internal fun httpStreamRepo(client: HttpClient): StreamRepo =
    StreamRepo { videoId, idToken ->
        either {
            val videoResponse =
                client.safeGet<JsonElement>("$apiVierVijfZes/content/${videoId.videoUuid}") {
                    headers { append(HttpHeaders.AUTHORIZATION, idToken.token) }
                }.bind()

            if (videoResponse is JsonObject) {
                val videoObj = videoResponse.jsonObject
                val resolvedStream =
                    when {
                        videoObj.containsKey("videoDash") ->
                            catch {
                                val drmKey = videoObj.getValue("drmKey").jsonObject.getValue("S").jsonPrimitive.content
                                val drmResponseJson =
                                    client.safeGet<JsonObject>("$apiGoPlay/video/xml/${drmKey}") {
                                        headers { append(HttpHeaders.AUTHORIZATION, idToken.token) }
                                    }.bind()
                                val auth = catch(drmResponseJson.getValue("auth").jsonPrimitive::content).mapLeft { ApiResponse.Failure.Stream.DrmAuth(drmResponseJson, it) }.bind()
                                ResolvedStream.MpegDash(
                                    videoId,
                                    videoObj.getValue("videoDash").jsonObject.getValue("S").jsonPrimitive.content,
                                    auth
                                )
                            }.mapLeft { ApiResponse.Failure.Stream.MpegDash(videoObj, it) }
                        videoObj.containsKey("video") ->
                            catch {
                                ResolvedStream.Hls(
                                    videoId,
                                    videoObj.getValue("video").jsonObject.getValue("S").jsonPrimitive.content
                                )
                            }.mapLeft { ApiResponse.Failure.Stream.Hls(videoObj, it) }
                        else -> ApiResponse.Failure.Stream.UnknownStream(videoObj).left()
                    }.bind()
                ApiResponse.Success.Stream(resolvedStream)
            } else {
                ApiResponse.Success.Stream(ResolvedStream.NoStreamFound(videoId))
            }
        }
    }

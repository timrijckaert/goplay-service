package be.tapped.vtmgo.profile

import arrow.core.Either
import arrow.core.ValidatedNel
import arrow.core.computations.either
import arrow.core.flatMap
import arrow.core.invalidNel
import arrow.core.left
import arrow.core.nonEmptyListOf
import arrow.core.right
import arrow.core.rightIfNotNull
import arrow.core.validNel
import arrow.core.zip
import be.tapped.common.internal.ReadOnlyCookieJar
import be.tapped.common.internal.executeAsync
import be.tapped.common.internal.jsonMediaType
import be.tapped.vtmgo.ApiResponse
import be.tapped.vtmgo.ApiResponse.Failure.Authentication
import be.tapped.vtmgo.common.safeBodyString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.Cookie
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody

public sealed interface JWTTokenRepo {
    public suspend fun login(
            userName: String,
            password: String,
    ): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.Token>
}

private inline class IdToken(val token: String)

internal class HttpAndroidJWTTokenRepo(private val client: OkHttpClient) : JWTTokenRepo {
    override suspend fun login(userName: String, password: String): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.Token> =
            either {
                withContext(Dispatchers.IO) {
                    // Start login flow
                    client.executeAsync(
                            Request.Builder()
                                    .url(
                                            HttpUrl.Builder()
                                                    .scheme("https")
                                                    .host("login2.vtm.be")
                                                    .addPathSegment("authorize")
                                                    .addQueryParameter("client_id", "vtm-go-android")
                                                    .addQueryParameter("response_type", "id_token")
                                                    .addQueryParameter("scope", "openid email profile address phone")
                                                    .addQueryParameter("nonce", "55007373265")
                                                    .addQueryParameter("sdkVersion", "0.13.1")
                                                    .addQueryParameter("state", "dnRtLWdvLWFuZHJvaWQ=") //vtm-go-android Base64
                                                    .addQueryParameter("redirect_uri", "https://login2.vtm.be/continue")
                                                    .build()
                                    )
                                    .get()
                                    .build()
                    )

                    // Send login credentials
                    client.executeAsync(
                            Request.Builder()
                                    .url(
                                            HttpUrl.Builder()
                                                    .scheme("https")
                                                    .host("login2.vtm.be")
                                                    .addPathSegment("login")
                                                    .addQueryParameter("client_id", "vtm-go-android")
                                                    .build()
                                    )
                                    .post(
                                            FormBody.Builder()
                                                    .addEncoded("userName", userName)
                                                    .addEncoded("password", password)
                                                    .build()
                                    )
                                    .build()
                    ).safeBodyString()

                    // Follow login
                    // We are redirected and our id_token is in the fragment of the redirected url
                    val idToken = client.executeAsync(
                            Request.Builder()
                                    .url(
                                            HttpUrl.Builder()
                                                    .scheme("https")
                                                    .host("login2.vtm.be")
                                                    .addPathSegment("authorize")
                                                    .addPathSegment("continue")
                                                    .addQueryParameter("client_id", "vtm-go-android")
                                                    .build())
                                    .get()
                                    .build()
                    ).request.url.fragment.idTokenFromFragment.bind()

                    // Okay, final stage. We now need to authorize our id_token so we get a valid JWT.
                    val json = !client.executeAsync(
                            Request.Builder()
                                    .url(
                                            HttpUrl.Builder()
                                                    .scheme("https")
                                                    .host("lfvp-api.dpgmedia.net")
                                                    .addPathSegment("authorize")
                                                    .addPathSegment("idToken")
                                                    .build()
                                    )
                                    .post(
                                            "${
                                                buildJsonObject {
                                                    put("clientId", "vtm-go-android")
                                                    put("pipIdToken", idToken.token)
                                                }
                                            }".toRequestBody()
                                    )
                                    .build()
                    ).safeBodyString()

                    val jwtToken = Either.catch { Json.decodeFromString<JsonObject>(json)["jsonWebToken"] }.mapLeft(ApiResponse.Failure::JsonParsingException)
                    println(jwtToken)

                    TODO()
                }
            }

    private val idTokenRegex by lazy { Regex("id_token=([^&]*)?") }

    private val String?.idTokenFromFragment: Either<Authentication.MissingIdToken, IdToken>
        get() =
            Either.fromNullable(this)
                    .flatMap { Either.fromNullable(idTokenRegex.find(it)?.groupValues?.get(1)).map(::IdToken) }
                    .mapLeft { Authentication.MissingIdToken }
}

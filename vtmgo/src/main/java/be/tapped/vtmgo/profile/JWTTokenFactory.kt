package be.tapped.vtmgo.profile

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.flatMap
import be.tapped.common.internal.executeAsync
import be.tapped.common.internal.jsonMediaType
import be.tapped.vtmgo.ApiResponse
import be.tapped.vtmgo.ApiResponse.Failure.Authentication
import be.tapped.vtmgo.common.HeaderBuilder
import be.tapped.vtmgo.common.safeBodyString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

public sealed interface JWTTokenRepo {
    public suspend fun login(
            userName: String,
            password: String,
    ): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.Token>
}

private inline class IdToken(val token: String)

internal class HttpAndroidJWTTokenRepo(
        private val client: OkHttpClient,
        private val headerBuilder: HeaderBuilder,
        private val jwtTokenJsonParser: JWTTokenJsonParser = JWTTokenJsonParser()
) : JWTTokenRepo {

    private val idTokenRegex by lazy { Regex("id_token=([^&]*)?") }

    override suspend fun login(userName: String, password: String): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.Token> =
            either {
                withContext(Dispatchers.IO) {
                    // Start login flow
                    client.executeAsync(
                            Request.Builder()
                                    .headers(headerBuilder.defaultHeaders)
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
                                    .headers(headerBuilder.defaultHeaders)
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
                                    .headers(headerBuilder.defaultHeaders)
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
                                    .headers(headerBuilder.defaultHeaders)
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
                                            }".toRequestBody(jsonMediaType)
                                    )
                                    .build()
                    ).safeBodyString()

                    ApiResponse.Success.Authentication.Token(jwtTokenJsonParser.parse(json).bind())
                }
            }

    private val String?.idTokenFromFragment: Either<Authentication.MissingIdToken, IdToken>
        get() =
            Either.fromNullable(this)
                    .flatMap { Either.fromNullable(idTokenRegex.find(it)?.groupValues?.get(1)).map(::IdToken) }
                    .mapLeft { Authentication.MissingIdToken }
}

internal class JWTTokenJsonParser {
    fun parse(json: String): Either<ApiResponse.Failure, TokenWrapper> =
            Either
                    .catch { TokenWrapper(JWT(Json.decodeFromString<JsonObject>(json)["jsonWebToken"]!!.jsonPrimitive.content)) }
                    .mapLeft { ApiResponse.Failure.JsonParsingException(it, json) }
}

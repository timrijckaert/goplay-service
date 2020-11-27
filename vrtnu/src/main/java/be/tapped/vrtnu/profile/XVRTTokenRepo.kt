package be.tapped.vrtnu.profile

import arrow.core.Either
import arrow.core.NonEmptyList
import be.tapped.common.ReadOnlyCookieJar
import be.tapped.common.executeAsync
import be.tapped.common.jsonMediaType
import be.tapped.vrtnu.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

interface XVRTTokenRepo {
    suspend fun fetchXVRTToken(userName: String, loginResponse: LoginResponse): Either<ApiResponse.Failure, XVRTToken>
}

internal class HttpXVRTTokenRepo(
    private val client: OkHttpClient,
    private val cookieJar: ReadOnlyCookieJar,
) : XVRTTokenRepo {

    companion object {
        private const val API_KEY = "3_qhEcPa5JGFROVwu5SWKqJ4mVOIkwlFNMSKwzPDAh8QZOtHqu6L4nD5Q7lk0eXOOG"
        private const val TOKEN_GATEWAY_URL = "https://token.vrt.be"
        private const val COOKIE_X_VRT_TOKEN = "X-VRT-Token"
    }

    override suspend fun fetchXVRTToken(userName: String, loginResponse: LoginResponse): Either<ApiResponse.Failure, XVRTToken> {
        val loginCookie = "glt_${API_KEY}=${loginResponse.loginToken}"
        val json = buildJsonObject {
            put("uid", loginResponse.uid)
            put("uidsig", loginResponse.uidSignature)
            put("ts", loginResponse.signatureTimestamp)
            put("email", userName)
        }.toString()

        return withContext(Dispatchers.IO) {
            client.executeAsync(
                Request.Builder()
                    .url(TOKEN_GATEWAY_URL)
                    .addHeader("Cookie", loginCookie)
                    .post(json.toRequestBody(jsonMediaType))
                    .build()
            )

            cookieJar.validateCookie(COOKIE_X_VRT_TOKEN)
                .map(::XVRTToken)
                .toEither()
                .mapLeft { ApiResponse.Failure.MissingCookieValues(NonEmptyList(COOKIE_X_VRT_TOKEN)) }
        }
    }
}

package be.tapped.vrtnu.authentication

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.Validated
import arrow.core.computations.either
import arrow.core.extensions.nonemptylist.semigroup.semigroup
import arrow.core.extensions.validated.applicative.applicative
import arrow.core.filterOrOther
import arrow.core.fix
import arrow.core.invalidNel
import arrow.core.validNel
import be.tapped.vrtnu.authentication.TokenProvider.TokenResponse.Failure.MissingCookieValues
import be.tapped.vtmgo.common.ReadOnlyCookieJar
import be.tapped.vtmgo.common.executeAsync
import be.tapped.vtmgo.common.jsonMediaType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

interface TokenProvider {
    sealed class TokenResponse {
        data class Success(val tokenWrapper: TokenWrapper) : TokenResponse()
        sealed class Failure : TokenResponse() {
            data class JsonLoginParsingException(val exception: Throwable) : Failure()
            data class IncorrectJsonLoginResponse(val loginResponseFailure: LoginResponse.LoginFailure) : Failure()
            data class MissingCookieValues(val cookieValues: NonEmptyList<String>) : Failure()
        }
    }

    suspend fun getTokenWrapper(userName: String, password: String): Either<TokenResponse.Failure, TokenResponse.Success>
}

internal class HttpTokenProvider(
    private val client: OkHttpClient,
    private val jsonLoginResponseMapper: JsonLoginResponseMapper,
    private val cookieJar: ReadOnlyCookieJar,
) : TokenProvider {

    companion object {
        private const val API_KEY = "3_qhEcPa5JGFROVwu5SWKqJ4mVOIkwlFNMSKwzPDAh8QZOtHqu6L4nD5Q7lk0eXOOG"

        private const val VRT_LOGIN_URL = "https://login.vrt.be/perform_login"
        private const val LOGIN_URL = "https://accounts.vrt.be/accounts.login"
        private const val TOKEN_GATEWAY_URL = "https://token.vrt.be"
        private const val USER_TOKEN_GATEWAY_URL = "https://token.vrt.be/vrtnuinitlogin?provider=site&destination=https://www.vrt.be/vrtnu/"

        private const val COOKIE_XSRF = "OIDCXSRF"
        private const val COOKIE_X_VRT_TOKEN = "X-VRT-Token"
        private const val COOKIE_VRT_LOGIN_AT = "vrtlogin-at"
        private const val COOKIE_VRT_LOGIN_RT = "vrtlogin-rt"
        private const val COOKIE_VRT_LOGIN_EXPIRY = "vrtlogin-expiry"
    }

    override suspend fun getTokenWrapper(
        userName: String,
        password: String,
    ): Either<TokenProvider.TokenResponse.Failure, TokenProvider.TokenResponse.Success> =
        either {
            val loginResponse = !getLoginResponse(userName, password)
            val xVRTToken = !fetchXVRTToken(userName, loginResponse)
            val token = !fetchToken(xVRTToken, loginResponse)
            TokenProvider.TokenResponse.Success(token)
        }

    private suspend fun getLoginResponse(userName: String, password: String): Either<TokenProvider.TokenResponse.Failure, LoginResponse> {
        val loginJson = client.executeAsync(
            Request.Builder()
                .url(LOGIN_URL)
                .post(
                    FormBody.Builder()
                        .add("loginID", userName)
                        .add("password", password)
                        .add("sessionExpiration", "-2")
                        .add("APIKey", API_KEY)
                        .add("targetEnv", "jssdk")
                        .build()
                )
                .build()
        )

        return jsonLoginResponseMapper.parse(Json.decodeFromString(loginJson.body!!.string()))
            .mapLeft { TokenProvider.TokenResponse.Failure.JsonLoginParsingException(it) }
            .filterOrOther({ it.isValid }, { TokenProvider.TokenResponse.Failure.IncorrectJsonLoginResponse(it.loginFailure) })
    }

    private suspend fun fetchToken(xVRTToken: XVRTToken, login: LoginResponse): Either<TokenProvider.TokenResponse.Failure, TokenWrapper> {
        client.executeAsync(
            Request.Builder()
                .get()
                .url(USER_TOKEN_GATEWAY_URL)
                .build()
        )

        //TODO catch if not found in CookieJar
        val xsrf = OIDCXSRF(cookieJar[COOKIE_XSRF]!!)

        client.executeAsync(
            Request.Builder()
                .url(VRT_LOGIN_URL)
                .post(
                    FormBody.Builder()
                        .add("UID", login.uid)
                        .add("UIDSignature", login.uidSignature)
                        .add("signatureTimestamp", login.signatureTimestamp)
                        .add("client_id", "vrtnu-site")
                        .add("_csrf", xsrf.token)
                        .build()
                )
                .build()
        )

        val accessTokenValidated = validateCookie(COOKIE_VRT_LOGIN_AT).map(::AccessToken)
        val refreshTokenValidated = validateCookie(COOKIE_VRT_LOGIN_RT).map(::RefreshToken)
        val expiryValidated = validateCookie(COOKIE_VRT_LOGIN_EXPIRY).map { Expiry(it.toLong()) }

        return either {
            val (accessToken, refreshToken, expiry) = !Validated.applicative(NonEmptyList.semigroup<MissingCookieValues>())
                .tupledN(accessTokenValidated, refreshTokenValidated, expiryValidated)
                .fix()
                .toEither()

            TokenWrapper(
                xVRTToken,
                accessToken,
                refreshToken,
                expiry,
            )
        }
    }

    private suspend fun fetchXVRTToken(userName: String, loginResponse: LoginResponse): Either<TokenProvider.TokenResponse.Failure, XVRTToken> {
        val loginCookie = "glt_${API_KEY}=${loginResponse.loginToken}"
        val json = buildJsonObject {
            put("uid", loginResponse.uid)
            put("uidsig", loginResponse.uidSignature)
            put("ts", loginResponse.signatureTimestamp)
            put("email", userName)
        }.toString()

        client.executeAsync(
            Request.Builder()
                .url(TOKEN_GATEWAY_URL)
                .addHeader("Cookie", loginCookie)
                .post(json.toRequestBody(jsonMediaType))
                .build()
        )
        return validateCookie(COOKIE_X_VRT_TOKEN)
            .map(::XVRTToken).toEither().mapLeft { MissingCookieValues(NonEmptyList(COOKIE_X_VRT_TOKEN)) }
    }

    private fun validateCookie(cookieName: String): Validated<NonEmptyList<MissingCookieValues>, String> =
        cookieJar[cookieName]?.let { it.validNel() } ?: MissingCookieValues(NonEmptyList(cookieName)).invalidNel()
}

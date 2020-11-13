package be.tapped.vrtnu.authentication

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.Validated
import arrow.core.computations.either
import arrow.core.extensions.nonemptylist.semigroup.semigroup
import arrow.core.extensions.validated.applicative.applicative
import arrow.core.extensions.validated.bifunctor.mapLeft
import arrow.core.filterOrOther
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
            object EmptyJson : Failure()
        }
    }

    suspend fun fetchTokenWrapper(userName: String, password: String): Either<TokenResponse.Failure, TokenResponse.Success>
}

interface LoginRepo {
    suspend fun fetchLoginResponse(userName: String, password: String): Either<TokenProvider.TokenResponse.Failure, LoginResponse>
}

internal class HttpLoginRepo(
    private val client: OkHttpClient,
    private val jsonLoginResponseMapper: JsonLoginResponseMapper,
) : LoginRepo {
    companion object {
        private const val API_KEY = "3_qhEcPa5JGFROVwu5SWKqJ4mVOIkwlFNMSKwzPDAh8QZOtHqu6L4nD5Q7lk0eXOOG"
        private const val LOGIN_URL = "https://accounts.vrt.be/accounts.login"
    }

    override suspend fun fetchLoginResponse(userName: String, password: String): Either<TokenProvider.TokenResponse.Failure, LoginResponse> {
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

        return either {
            val rawLoginJson = !Either.fromNullable(loginJson.body?.string()).mapLeft { TokenProvider.TokenResponse.Failure.EmptyJson }
            val loginResponse = jsonLoginResponseMapper.parse(Json.decodeFromString(rawLoginJson))

            !loginResponse.mapLeft(TokenProvider.TokenResponse.Failure::JsonLoginParsingException)
                .filterOrOther(LoginResponse::isValid) { TokenProvider.TokenResponse.Failure.IncorrectJsonLoginResponse(it.loginFailure) }
        }
    }
}

interface XVRTTokenRepo {
    suspend fun fetchXVRTToken(userName: String, loginResponse: LoginResponse): Either<TokenProvider.TokenResponse.Failure, XVRTToken>
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

    override suspend fun fetchXVRTToken(userName: String, loginResponse: LoginResponse): Either<TokenProvider.TokenResponse.Failure, XVRTToken> {
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
        return cookieJar.validateCookie(COOKIE_X_VRT_TOKEN)
            .map(::XVRTToken)
            .toEither()
            .mapLeft { MissingCookieValues(NonEmptyList(COOKIE_X_VRT_TOKEN)) }
    }
}

interface OIDCXSRFRepo {
    suspend fun fetchXSRFToken(): Either<MissingCookieValues, OIDCXSRF>
}

internal class HttpOIDCXSRFRepo(
    private val client: OkHttpClient,
    private val cookieJar: ReadOnlyCookieJar,
) : OIDCXSRFRepo {

    companion object {
        private const val USER_TOKEN_GATEWAY_URL = "https://token.vrt.be/vrtnuinitlogin?provider=site&destination=https://www.vrt.be/vrtnu/"
        private const val COOKIE_XSRF = "OIDCXSRF"
    }

    override suspend fun fetchXSRFToken(): Either<MissingCookieValues, OIDCXSRF> {
        client.executeAsync(
            Request.Builder()
                .get()
                .url(USER_TOKEN_GATEWAY_URL)
                .build()
        )

        return cookieJar.validateCookie(COOKIE_XSRF).map(::OIDCXSRF).mapLeft(::MissingCookieValues).toEither()
    }
}

internal class HttpTokenProvider(
    private val client: OkHttpClient,
    private val cookieJar: ReadOnlyCookieJar,
    private val loginRepo: LoginRepo = HttpLoginRepo(client, JsonLoginResponseMapper),
    private val xVRTTokenRepo: XVRTTokenRepo = HttpXVRTTokenRepo(client, cookieJar),
    oIDCXSRFRepo: OIDCXSRFRepo = HttpOIDCXSRFRepo(client, cookieJar),
) : TokenProvider,
    LoginRepo by loginRepo,
    XVRTTokenRepo by xVRTTokenRepo,
    OIDCXSRFRepo by oIDCXSRFRepo {

    companion object {
        private const val VRT_LOGIN_URL = "https://login.vrt.be/perform_login"
        private const val COOKIE_VRT_LOGIN_AT = "vrtlogin-at"
        private const val COOKIE_VRT_LOGIN_RT = "vrtlogin-rt"
        private const val COOKIE_VRT_LOGIN_EXPIRY = "vrtlogin-expiry"
    }

    override suspend fun fetchTokenWrapper(
        userName: String,
        password: String,
    ): Either<TokenProvider.TokenResponse.Failure, TokenProvider.TokenResponse.Success> =
        either {
            val loginResponse = !fetchLoginResponse(userName, password)
            val xVRTToken = !fetchXVRTToken(userName, loginResponse)
            val oidcXSRFToken = !fetchXSRFToken()
            val token = !fetchToken(xVRTToken, oidcXSRFToken, loginResponse)
            TokenProvider.TokenResponse.Success(token)
        }

    private suspend fun fetchToken(
        xVRTToken: XVRTToken,
        oidcXSRF: OIDCXSRF,
        login: LoginResponse,
    ): Either<TokenProvider.TokenResponse.Failure, TokenWrapper> {
        client.executeAsync(
            Request.Builder()
                .url(VRT_LOGIN_URL)
                .post(
                    FormBody.Builder()
                        .add("UID", login.uid)
                        .add("UIDSignature", login.uidSignature)
                        .add("signatureTimestamp", login.signatureTimestamp)
                        .add("client_id", "vrtnu-site")
                        .add("_csrf", oidcXSRF.token)
                        .build()
                )
                .build()
        )

        return either {
            val (accessToken, refreshToken, expiry) = !Validated.applicative(NonEmptyList.semigroup<String>())
                .tupledN(
                    cookieJar.validateCookie(COOKIE_VRT_LOGIN_AT).map(::AccessToken),
                    cookieJar.validateCookie(COOKIE_VRT_LOGIN_RT).map(::RefreshToken),
                    cookieJar.validateCookie(COOKIE_VRT_LOGIN_EXPIRY).map { Expiry(it.toLong()) }
                )
                .mapLeft(::MissingCookieValues)
                .toEither()

            TokenWrapper(
                xVRTToken,
                accessToken,
                refreshToken,
                expiry,
            )
        }
    }
}

internal fun ReadOnlyCookieJar.validateCookie(cookieName: String): Validated<NonEmptyList<String>, String> =
    this[cookieName]?.validNel() ?: cookieName.invalidNel()

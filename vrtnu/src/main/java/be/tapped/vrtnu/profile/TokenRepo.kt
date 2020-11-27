package be.tapped.vrtnu.profile

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.Validated
import arrow.core.computations.either
import arrow.core.extensions.nonemptylist.semigroup.semigroup
import arrow.core.extensions.validated.applicative.applicative
import arrow.core.extensions.validated.bifunctor.mapLeft
import arrow.core.invalidNel
import arrow.core.validNel
import be.tapped.common.DefaultCookieJar
import be.tapped.common.ReadOnlyCookieJar
import be.tapped.common.executeAsync
import be.tapped.vrtnu.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.closeQuietly

interface TokenRepo {

    suspend fun fetchTokenWrapper(userName: String, password: String): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.Token>

    suspend fun refreshTokenWrapper(refreshToken: RefreshToken): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.Token>

    suspend fun fetchXVRTToken(userName: String, password: String): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.VRTToken>

    suspend fun fetchVRTPlayerToken(xVRTToken: XVRTToken): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.PlayerToken>

}

internal class HttpTokenRepo(
    private val client: OkHttpClient,
    private val cookieJar: ReadOnlyCookieJar,
    loginRepo: LoginRepo = HttpLoginRepo(client, JsonLoginResponseMapper),
    xVRTTokenRepo: XVRTTokenRepo = HttpXVRTTokenRepo(client, cookieJar),
    oIDCXSRFRepo: OIDCXSRFRepo = HttpOIDCXSRFRepo(client, cookieJar),
    playerTokenRepo: PlayerTokenRepo = HttpPlayerTokenRepo(client, JsonVRTPlayerTokenParser()),
) : TokenRepo,
    LoginRepo by loginRepo,
    XVRTTokenRepo by xVRTTokenRepo,
    OIDCXSRFRepo by oIDCXSRFRepo,
    PlayerTokenRepo by playerTokenRepo {

    companion object {
        private const val VRT_LOGIN_URL = "https://login.vrt.be/perform_login"
        private const val COOKIE_VRT_LOGIN_AT = "vrtlogin-at"
        private const val COOKIE_VRT_LOGIN_RT = "vrtlogin-rt"
        private const val COOKIE_VRT_LOGIN_EXPIRY = "vrtlogin-expiry"
        private const val TOKEN_GATEWAY_URL = "https://token.vrt.be"
    }

    override suspend fun fetchTokenWrapper(
        userName: String,
        password: String,
    ): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.Token> =
        withContext(Dispatchers.IO) {
            either {
                val loginResponse = !fetchLoginResponse(userName, password)
                val oidcXSRFToken = !fetchXSRFToken()
                client.executeAsync(
                    Request.Builder()
                        .url(VRT_LOGIN_URL)
                        .post(
                            FormBody.Builder()
                                .add("UID", loginResponse.uid)
                                .add("UIDSignature", loginResponse.uidSignature)
                                .add("signatureTimestamp", loginResponse.signatureTimestamp)
                                .add("client_id", "vrtnu-site")
                                .add("_csrf", oidcXSRFToken.token)
                                .build()
                        )
                        .build()
                ).closeQuietly()
                !fetchTokenWrapperFromCookieJar(cookieJar)
            }
        }

    override suspend fun refreshTokenWrapper(refreshToken: RefreshToken): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.Token> =
        withContext(Dispatchers.IO) {
            val newCookieJar = DefaultCookieJar()
            client
                .newBuilder()
                .cookieJar(newCookieJar)
                .build()
                .executeAsync(
                    Request.Builder()
                        .get()
                        .header("Cookie", "vrtlogin-rt=${refreshToken.token}")
                        .url("$TOKEN_GATEWAY_URL/refreshtoken?legacy=true")
                        .build()
                ).closeQuietly()

            fetchTokenWrapperFromCookieJar(newCookieJar)
        }

    override suspend fun fetchXVRTToken(
        userName: String,
        password: String,
    ): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.VRTToken> =
        either {
            val loginResponse = !fetchLoginResponse(userName, password)
            ApiResponse.Success.Authentication.VRTToken(!fetchXVRTToken(userName, loginResponse))
        }

    private suspend fun fetchTokenWrapperFromCookieJar(cookieJar: ReadOnlyCookieJar): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.Token> =
        either {
            val (accessToken, newRefreshToken, expiry) = !Validated.applicative(NonEmptyList.semigroup<String>())
                .tupledN(
                    cookieJar.validateCookie(COOKIE_VRT_LOGIN_AT).map(::AccessToken),
                    cookieJar.validateCookie(COOKIE_VRT_LOGIN_RT).map(::RefreshToken),
                    cookieJar.validateCookie(COOKIE_VRT_LOGIN_EXPIRY).map { Expiry(it.toLong()) }
                )
                .mapLeft(ApiResponse.Failure.Authentication::MissingCookieValues)
                .toEither()

            ApiResponse.Success.Authentication.Token(
                TokenWrapper(
                    accessToken,
                    newRefreshToken,
                    expiry,
                )
            )
        }
}

internal fun ReadOnlyCookieJar.validateCookie(cookieName: String): Validated<NonEmptyList<String>, String> =
    this[cookieName]?.validNel() ?: cookieName.invalidNel()

package be.tapped.vrtnu.authentication

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.Validated
import arrow.core.computations.either
import arrow.core.extensions.nonemptylist.semigroup.semigroup
import arrow.core.extensions.validated.applicative.applicative
import arrow.core.extensions.validated.bifunctor.mapLeft
import arrow.core.invalidNel
import arrow.core.validNel
import be.tapped.vrtnu.authentication.TokenRepo.TokenResponse.Failure.MissingCookieValues
import be.tapped.vtmgo.common.DefaultCookieJar
import be.tapped.vtmgo.common.ReadOnlyCookieJar
import be.tapped.vtmgo.common.executeAsync
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

interface TokenRepo {
    sealed class TokenResponse {
        data class Success(val tokenWrapper: TokenWrapper) : TokenResponse()
        sealed class Failure : TokenResponse() {
            data class FailedToLoginException(val loginResponseFailure: LoginFailure) : Failure()
            data class MissingCookieValues(val cookieValues: NonEmptyList<String>) : Failure()
            object EmptyJson : Failure()
        }
    }

    suspend fun fetchTokenWrapper(userName: String, password: String): Either<TokenResponse.Failure, TokenResponse.Success>
    suspend fun refreshTokenWrapper(refreshToken: RefreshToken): Either<TokenResponse.Failure, TokenResponse.Success>
}

class HttpTokenRepo(
    private val client: OkHttpClient,
    private val cookieJar: ReadOnlyCookieJar,
    loginRepo: LoginRepo = HttpLoginRepo(client, JsonLoginResponseMapper),
    xVRTTokenRepo: XVRTTokenRepo = HttpXVRTTokenRepo(client, cookieJar),
    oIDCXSRFRepo: OIDCXSRFRepo = HttpOIDCXSRFRepo(client, cookieJar),
) : TokenRepo,
    LoginRepo by loginRepo,
    XVRTTokenRepo by xVRTTokenRepo,
    OIDCXSRFRepo by oIDCXSRFRepo {

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
    ): Either<TokenRepo.TokenResponse.Failure, TokenRepo.TokenResponse.Success> =
        either {
            val loginResponse = !fetchLoginResponse(userName, password)
            val oidcXSRFToken = !fetchXSRFToken()
            !fetchToken(oidcXSRFToken, loginResponse)
        }

    private suspend fun fetchToken(
        oidcXSRF: OIDCXSRF,
        login: LoginResponse,
    ): Either<TokenRepo.TokenResponse.Failure, TokenRepo.TokenResponse.Success> {
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

        return fetchTokenWrapperFromCookieJar(cookieJar)
    }

    override suspend fun refreshTokenWrapper(refreshToken: RefreshToken): Either<TokenRepo.TokenResponse.Failure, TokenRepo.TokenResponse.Success> {
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
            )

        return fetchTokenWrapperFromCookieJar(newCookieJar)
    }

    private suspend fun fetchTokenWrapperFromCookieJar(cookieJar: ReadOnlyCookieJar): Either<TokenRepo.TokenResponse.Failure, TokenRepo.TokenResponse.Success> {
        return either {
            val (accessToken, newRefreshToken, expiry) = !Validated.applicative(NonEmptyList.semigroup<String>())
                .tupledN(
                    cookieJar.validateCookie(COOKIE_VRT_LOGIN_AT).map(::AccessToken),
                    cookieJar.validateCookie(COOKIE_VRT_LOGIN_RT).map(::RefreshToken),
                    cookieJar.validateCookie(COOKIE_VRT_LOGIN_EXPIRY).map { Expiry(it.toLong()) }
                )
                .mapLeft(::MissingCookieValues)
                .toEither()

            TokenRepo.TokenResponse.Success(
                TokenWrapper(
                    accessToken,
                    newRefreshToken,
                    expiry,
                )
            )
        }
    }
}

internal fun ReadOnlyCookieJar.validateCookie(cookieName: String): Validated<NonEmptyList<String>, String> =
    this[cookieName]?.validNel() ?: cookieName.invalidNel()

package be.tapped.vrtnu.authentication

import arrow.core.*
import arrow.core.computations.either
import arrow.core.extensions.nonemptylist.semigroup.semigroup
import arrow.core.extensions.validated.applicative.applicative
import arrow.core.extensions.validated.bifunctor.mapLeft
import be.tapped.vrtnu.authentication.TokenRepo.TokenResponse.Failure.MissingCookieValues
import be.tapped.vtmgo.common.DefaultCookieJar
import be.tapped.vtmgo.common.ReadOnlyCookieJar
import be.tapped.vtmgo.common.executeAsync
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

interface TokenRepo {
    sealed class TokenResponse {
        sealed class Success : TokenResponse() {
            data class Token(val tokenWrapper: TokenWrapper) : Success()
            data class PlayerToken(val vrtPlayerToken: VRTPlayerToken) : Success()
            data class VRTToken(val xVRTToken: XVRTToken) : Success()
        }

        sealed class Failure : TokenResponse() {
            data class JsonParsingException(val throwable: Throwable) : Failure()
            data class FailedToLogin(val loginResponseFailure: LoginFailure) : Failure()
            data class MissingCookieValues(val cookieValues: NonEmptyList<String>) : Failure()
            object EmptyJson : Failure()
        }
    }

    suspend fun fetchTokenWrapper(userName: String, password: String): Either<TokenResponse.Failure, TokenResponse.Success.Token>

    suspend fun refreshTokenWrapper(refreshToken: RefreshToken): Either<TokenResponse.Failure, TokenResponse.Success.Token>

    suspend fun fetchXVRTToken(userName: String, password: String): Either<TokenResponse.Failure, TokenResponse.Success.VRTToken>

    suspend fun fetchVRTPlayerToken(xVRTToken: XVRTToken): Either<TokenResponse.Failure, TokenResponse.Success.PlayerToken>

}

class HttpTokenRepo(
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
    ): Either<TokenRepo.TokenResponse.Failure, TokenRepo.TokenResponse.Success.Token> =
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
                )
                !fetchTokenWrapperFromCookieJar(cookieJar)
            }
        }

    override suspend fun refreshTokenWrapper(refreshToken: RefreshToken): Either<TokenRepo.TokenResponse.Failure, TokenRepo.TokenResponse.Success.Token> =
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
                )

            fetchTokenWrapperFromCookieJar(newCookieJar)
        }

    override suspend fun fetchXVRTToken(
        userName: String,
        password: String,
    ): Either<TokenRepo.TokenResponse.Failure, TokenRepo.TokenResponse.Success.VRTToken> =
        either {
            val loginResponse = !fetchLoginResponse(userName, password)
            TokenRepo.TokenResponse.Success.VRTToken(!fetchXVRTToken(userName, loginResponse))
        }

    private suspend fun fetchTokenWrapperFromCookieJar(cookieJar: ReadOnlyCookieJar): Either<TokenRepo.TokenResponse.Failure, TokenRepo.TokenResponse.Success.Token> =
        either {
            val (accessToken, newRefreshToken, expiry) = !Validated.applicative(NonEmptyList.semigroup<String>())
                .tupledN(
                    cookieJar.validateCookie(COOKIE_VRT_LOGIN_AT).map(::AccessToken),
                    cookieJar.validateCookie(COOKIE_VRT_LOGIN_RT).map(::RefreshToken),
                    cookieJar.validateCookie(COOKIE_VRT_LOGIN_EXPIRY).map { Expiry(it.toLong()) }
                )
                .mapLeft(::MissingCookieValues)
                .toEither()

            TokenRepo.TokenResponse.Success.Token(
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

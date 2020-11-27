package be.tapped.vtmgo.profile

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.Validated
import arrow.core.ValidatedNel
import arrow.core.computations.either
import arrow.core.extensions.nonemptylist.semigroup.semigroup
import arrow.core.extensions.validated.applicative.applicative
import arrow.core.extensions.validated.bifunctor.mapLeft
import arrow.core.filterOrElse
import arrow.core.invalidNel
import arrow.core.left
import arrow.core.right
import arrow.core.rightIfNotNull
import arrow.core.validNel
import be.tapped.common.ReadOnlyCookieJar
import be.tapped.common.executeAsync
import be.tapped.vtmgo.ApiResponse
import be.tapped.vtmgo.ApiResponse.Failure.Authentication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import javax.security.auth.login.LoginException

interface JWTTokenFactory {
    suspend fun login(
        userName: String,
        password: String,
    ): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.Token>
}

internal class VTMGOJWTTokenFactory(
    private val client: OkHttpClient,
    private val vtmCookieJar: ReadOnlyCookieJar,
) : JWTTokenFactory {

    companion object {
        private const val COOKIE_LFVP_AUTH = "lfvp_auth"
        private const val COOKIE_LFVP_AUTH_STATE = "lfvp_auth.state"
        private const val COOKIE_X_OIDCP_DEBUGID = "x-oidcp-debugid"
        private const val COOKIE_X_OIDCP_TICKET = "x-oidcp-ticket"
    }

    private val codeRegex = Regex("name=\"code\" value=\"([^\"]+)")
    private val stateRegex = Regex("name=\"state\" value=\"([^\"]+)")

    private val authState get() = validateCookie(COOKIE_LFVP_AUTH_STATE)
    private val debugId get() = validateCookie(COOKIE_X_OIDCP_DEBUGID)
    private val ticket get() = validateCookie(COOKIE_X_OIDCP_TICKET)

    /**
     * https://github.com/add-ons/plugin.video.vtm.go/wiki/Authentication-API
     */
    override suspend fun login(
        userName: String,
        password: String,
    ): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.Token> =
        either {
            !initLogin()

            !Validated.applicative(NonEmptyList.semigroup<String>())
                .tupledN(authState, debugId, ticket)
                .mapLeft(Authentication::MissingCookieValues)
                .toEither()

            !webLogin(userName, password)

            val authorizeHtmlResponse = !authorize()
            val code = !findCode(authorizeHtmlResponse)
            val state = !findState(authorizeHtmlResponse)

            !logInCallback(state, code)
            ApiResponse.Success.Authentication.Token(
                !getJWT().filterOrElse(
                    { isValidToken(it) },
                    { Authentication.JWTTokenNotValid })
            )
        }

    private suspend fun initLogin(): Either<ApiResponse.Failure, Unit> =
        withContext(Dispatchers.IO) {
            client.executeAsync(
                Request.Builder()
                    .get()
                    .url("https://vtm.be/vtmgo/aanmelden?redirectUrl=https://vtm.be/vtmgo")
                    .build()
            ).use { response ->
                if (!response.isSuccessful) response.toNetworkException()
                else Unit.right()
            }
        }

    private suspend fun webLogin(
        userName: String,
        password: String,
    ): Either<ApiResponse.Failure, Unit> =
        withContext(Dispatchers.IO) {
            client.executeAsync(
                Request.Builder()
                    .url("https://login2.vtm.be/login?client_id=vtm-go-web")
                    .post(
                        FormBody.Builder()
                            .addEncoded("userName", userName)
                            .addEncoded("password", password)
                            .add("jsEnabled", "true")
                            .build()
                    )
                    .build()
            ).use { response ->
                if (!response.isSuccessful) response.toNetworkException()
                else Unit.right()
            }
        }

    private suspend fun authorize(): Either<ApiResponse.Failure, String> =
        withContext(Dispatchers.IO) {
            client.executeAsync(
                Request.Builder()
                    .get()
                    .url("https://login2.vtm.be/authorize/continue?client_id=vtm-go-web")
                    .build()
            ).use { authorizeResponse ->
                if (!authorizeResponse.isSuccessful) authorizeResponse.toNetworkException()
                else authorizeResponse.body?.let(ResponseBody::string)?.right()
                    ?: Authentication.NoAuthorizeResponse.left()
            }
        }

    private fun findCode(authorizeHtmlResponse: String): Either<Authentication, String> =
        codeRegex.find(authorizeHtmlResponse)?.let { it.groups[1]?.value }?.right()
            ?: Authentication.NoCodeFound.left()

    private fun findState(authorizeHtmlResponse: String): Either<Authentication, String> =
        stateRegex.find(authorizeHtmlResponse)?.let { it.groups[1]?.value }?.right()
            ?: Authentication.NoStateFound.left()

    private suspend fun logInCallback(
        state: String,
        code: String,
    ): Either<ApiResponse.Failure, Unit> =
        withContext(Dispatchers.IO) {
            client.executeAsync(
                Request.Builder()
                    .url("https://vtm.be/vtmgo/login-callback")
                    .post(
                        FormBody.Builder()
                            .add("state", state)
                            .add("code", code)
                            .build()
                    )
                    .build()
            ).use { loginCallbackResponse ->
                if (!loginCallbackResponse.isSuccessful) loginCallbackResponse.toNetworkException()
                else Unit.right()
            }
        }

    private fun getJWT(): Either<Authentication, JWT> =
        vtmCookieJar[COOKIE_LFVP_AUTH]?.let(::JWT)
            .rightIfNotNull { Authentication.MissingCookieValues(NonEmptyList(COOKIE_LFVP_AUTH)) }

    private suspend fun isValidToken(jwtToken: JWT): Boolean =
        //TODO do we really need an extra lib for this?
        Either.catch { com.auth0.jwt.JWT.decode(jwtToken.token) }
            .fold({ false }, { true })

    private fun validateCookie(cookieName: String): ValidatedNel<String, String> =
        vtmCookieJar[cookieName]?.let { it.validNel() } ?: cookieName.invalidNel()

    private fun Response.toNetworkException(): Either<ApiResponse.Failure, Nothing> =
        ApiResponse.Failure.NetworkFailure(code, request).left()

}

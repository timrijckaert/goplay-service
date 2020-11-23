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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody

sealed class LoginException {
    data class NetworkException(val networkFailure: NetworkFailure) : LoginException()
    data class MissingCookieValues(val cookieValues: NonEmptyList<String>) : LoginException()
    object NoAuthorizeResponse : LoginException()
    object NoCodeFound : LoginException()
    object NoStateFound : LoginException()
    object JWTTokenNotValid : LoginException()
}

interface JWTTokenFactory {
    suspend fun login(
        userName: String,
        password: String,
    ): Either<LoginException, JWT>
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
    ): Either<LoginException, JWT> =
        either {
            !initLogin()

            !Validated.applicative(NonEmptyList.semigroup<String>())
                .tupledN(authState, debugId, ticket)
                .mapLeft(LoginException::MissingCookieValues)
                .toEither()

            !webLogin(userName, password)

            val authorizeHtmlResponse = !authorize()
            val code = !findCode(authorizeHtmlResponse)
            val state = !findState(authorizeHtmlResponse)

            !logInCallback(state, code)
            !getJWT().filterOrElse(
                { isValidToken(it) },
                { LoginException.JWTTokenNotValid })
        }

    private suspend fun initLogin(): Either<LoginException, Unit> =
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
    ): Either<LoginException, Unit> =
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

    private suspend fun authorize(): Either<LoginException, String> =
        withContext(Dispatchers.IO) {
            client.executeAsync(
                Request.Builder()
                    .get()
                    .url("https://login2.vtm.be/authorize/continue?client_id=vtm-go-web")
                    .build()
            ).use { authorizeResponse ->
                if (!authorizeResponse.isSuccessful) authorizeResponse.toNetworkException()
                else authorizeResponse.body?.let(ResponseBody::string)?.right()
                    ?: LoginException.NoAuthorizeResponse.left()
            }
        }

    private fun findCode(authorizeHtmlResponse: String): Either<LoginException, String> =
        codeRegex.find(authorizeHtmlResponse)?.let { it.groups[1]?.value }?.right()
            ?: LoginException.NoCodeFound.left()

    private fun findState(authorizeHtmlResponse: String): Either<LoginException, String> =
        stateRegex.find(authorizeHtmlResponse)?.let { it.groups[1]?.value }?.right()
            ?: LoginException.NoStateFound.left()

    private suspend fun logInCallback(
        state: String,
        code: String,
    ): Either<LoginException, Unit> =
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

    private fun getJWT(): Either<LoginException, JWT> =
        vtmCookieJar[COOKIE_LFVP_AUTH]?.let(::JWT)
            .rightIfNotNull { LoginException.MissingCookieValues(NonEmptyList(COOKIE_LFVP_AUTH)) }

    private suspend fun isValidToken(jwtToken: JWT): Boolean =
        //TODO do we really need an extra lib for this?
        Either.catch { com.auth0.jwt.JWT.decode(jwtToken.token) }
            .fold({ false }, { true })

    private fun validateCookie(cookieName: String): ValidatedNel<String, String> =
        vtmCookieJar[cookieName]?.let { it.validNel() } ?: cookieName.invalidNel()

    private fun Response.toNetworkException(): Either<LoginException.NetworkException, Nothing> =
        LoginException.NetworkException(NetworkFailure(request.url.toString(), code)).left()

}

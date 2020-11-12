package be.tapped.vtmgo.authentication

import arrow.core.*
import arrow.core.computations.either
import arrow.core.extensions.nonemptylist.semigroup.semigroup
import arrow.core.extensions.validated.applicative.applicative
import arrow.core.extensions.validated.bifunctor.mapLeft
import com.moczul.ok2curl.CurlInterceptor
import okhttp3.*
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

inline class JWT(val token: String)

sealed class LoginException {
    data class NetworkException(val url: String, val code: Int) : LoginException()
    data class MissingCookieValue(val cookieValue: String) : LoginException()
    object NoAuthorizeResponse : LoginException()
    object NoCodeFound : LoginException()
    object NoStateFound : LoginException()
}

class VTMTokenProvider(private val vtmCookieJar: VTMCookieJar = VTMCookieJar()) {

    companion object {
        private const val COOKIE_LFVP_AUTH = "lfvp_auth"
        private const val COOKIE_LFVP_AUTH_STATE = "lfvp_auth.state"
        private const val COOKIE_X_OIDCP_DEBUGID = "x-oidcp-debugid"
        private const val COOKIE_X_OIDCP_TICKET = "x-oidcp-ticket"
    }

    private val client =
        OkHttpClient.Builder()
            .addNetworkInterceptor(CurlInterceptor { message -> println("$message\n\r") })
            .cookieJar(vtmCookieJar)
            .build()

    private val codeRegex = Regex("name=\"code\" value=\"([^\"]+)")
    private val stateRegex = Regex("name=\"state\" value=\"([^\"]+)")

    /**
     * https://github.com/add-ons/plugin.video.vtm.go/wiki/Authentication-API
     */
    suspend fun login(userName: String, password: String): Either<LoginException, JWT> =
        either {
            !initLogin()

            // Either bind() ?
            Validated.applicative(NonEmptyList.semigroup<LoginException>())
                .tupledN(validateAuthState(), validateDebugId(), validateTicket())
                .mapLeft { it.isEmpty() }
                .toEither()

            !logIn(userName, password)

            val authorizeHtmlResponse = !authorize()
            val code = !findCode(authorizeHtmlResponse)
            val state = !findState(authorizeHtmlResponse)

            !logInCallback(state, code)
            !getJWT()
        }

    private suspend fun initLogin(): Either<LoginException, Unit> {
        val initLoginResponse = client.executeAsync(
            Request.Builder()
                .get()
                .url("https://vtm.be/vtmgo/aanmelden?redirectUrl=https://vtm.be/vtmgo")
                .build()
        )

        return if (!initLoginResponse.isSuccessful) initLoginResponse.toNetworkException()
        else Unit.right()
    }

    suspend fun isValidToken(jwtToken: JWT): Boolean =
        Either.catch { com.auth0.jwt.JWT.decode(jwtToken.token) }
            .fold({ false }, { true })

    private fun validateAuthState(): ValidatedNel<LoginException, Unit> =
        validateCookie(COOKIE_LFVP_AUTH_STATE)

    private fun validateDebugId(): ValidatedNel<LoginException, Unit> =
        validateCookie(COOKIE_X_OIDCP_DEBUGID)

    private fun validateTicket(): ValidatedNel<LoginException, Unit> =
        validateCookie(COOKIE_X_OIDCP_TICKET)

    private suspend fun logIn(userName: String, password: String): Either<LoginException, Unit> {
        val loginResponse = client.executeAsync(
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
        )

        return if (!loginResponse.isSuccessful) loginResponse.toNetworkException()
        else Unit.right()
    }

    private suspend fun authorize(): Either<LoginException, String> {
        val authorizeResponse = client.executeAsync(
            Request.Builder()
                .get()
                .url("https://login2.vtm.be/authorize/continue?client_id=vtm-go-web")
                .build()
        )

        return if (!authorizeResponse.isSuccessful) authorizeResponse.toNetworkException()
        else authorizeResponse.body?.let(ResponseBody::string)?.right()
            ?: LoginException.NoAuthorizeResponse.left()
    }

    private fun findCode(authorizeHtmlResponse: String): Either<LoginException, String> =
        codeRegex.find(authorizeHtmlResponse)?.let { it.groups[1]?.value }?.right()
            ?: LoginException.NoCodeFound.left()

    private fun findState(authorizeHtmlResponse: String): Either<LoginException, String> =
        stateRegex.find(authorizeHtmlResponse)?.let { it.groups[1]?.value }?.right()
            ?: LoginException.NoStateFound.left()

    private suspend fun logInCallback(state: String, code: String): Either<LoginException, Unit> {
        val loginCallbackResponse = client.executeAsync(
            Request.Builder()
                .url("https://vtm.be/vtmgo/login-callback")
                .post(
                    FormBody.Builder()
                        .add("state", state)
                        .add("code", code)
                        .build()
                )
                .build()
        )

        return if (!loginCallbackResponse.isSuccessful) loginCallbackResponse.toNetworkException()
        else Unit.right()
    }

    private fun getJWT(): Either<LoginException, JWT> =
        vtmCookieJar.getCookieValue(COOKIE_LFVP_AUTH)?.let(::JWT)
            .rightIfNotNull { LoginException.MissingCookieValue(COOKIE_LFVP_AUTH) }

    private fun Response.toNetworkException(): Either<LoginException.NetworkException, Nothing> =
        LoginException.NetworkException(request.url.toString(), code).left()

    private fun validateCookie(cookieName: String): ValidatedNel<LoginException, Unit> =
        vtmCookieJar.getCookieValue(cookieName)?.let { Unit.validNel() }
            ?: toMissingCookieValue(cookieName)

    private fun toMissingCookieValue(cookieName: String): ValidatedNel<LoginException, Nothing> =
        LoginException.MissingCookieValue(cookieName).invalidNel()

    private suspend fun OkHttpClient.executeAsync(request: Request): Response =
        suspendCoroutine { continuation ->
            newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    continuation.resume(response)
                }
            })
        }
}

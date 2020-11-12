package be.tapped.vtmgo.authentication

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import arrow.core.rightIfNotNull
import com.moczul.ok2curl.CurlInterceptor
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody

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
            !signUp()

            // Could rewrite to Validate where MissingCookieValue has a NonEmptyList of all failed validations
            !validateAuthState()
            !validateDebugId()
            !validateTicket()

            !logIn(userName, password)

            val authorizeHtmlResponse = !authorize()
            val code = !findCode(authorizeHtmlResponse)
            val state = !findState(authorizeHtmlResponse)

            !logInCallback(state, code)
            !getJWT()
        }

    private suspend fun signUp(): Either<LoginException, Unit> =
        client.newCall(
            Request.Builder()
                .get()
                .url("https://vtm.be/vtmgo/aanmelden?redirectUrl=https://vtm.be/vtmgo")
                .build()
        ).execute()
            .use {
                if (!it.isSuccessful) LoginException.NetworkException(
                    it.request.url.toString(),
                    it.code
                ).left()
                else Unit.right()
            }

    private fun validateAuthState(): Either<LoginException, Unit> =
        if (vtmCookieJar.getCookieValue(COOKIE_LFVP_AUTH_STATE) == null) LoginException.MissingCookieValue(
            COOKIE_LFVP_AUTH_STATE
        ).left()
        else Unit.right()

    private fun validateDebugId(): Either<LoginException, Unit> =
        if (vtmCookieJar.getCookieValue(COOKIE_X_OIDCP_DEBUGID) == null) LoginException.MissingCookieValue(
            COOKIE_X_OIDCP_DEBUGID
        ).left()
        else Unit.right()

    private fun validateTicket(): Either<LoginException, Unit> =
        if (vtmCookieJar.getCookieValue(COOKIE_X_OIDCP_TICKET) == null) LoginException.MissingCookieValue(
            COOKIE_X_OIDCP_TICKET
        ).left()
        else Unit.right()

    // Should rewrite to non-blocking suspend function using `suspendCoroutine`
    private suspend fun logIn(userName: String, password: String): Either<LoginException, Unit> =
        client
            .newCall(
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
            ).execute()
            .use {
                if (!it.isSuccessful) LoginException.NetworkException(
                    it.request.url.toString(),
                    it.code
                ).left()
                else Unit.right()
            }

    // Should rewrite to non-blocking suspend function using `suspendCoroutine`
    private suspend fun authorize(): Either<LoginException, String> =
        client.newCall(
            Request.Builder()
                .get()
                .url("https://login2.vtm.be/authorize/continue?client_id=vtm-go-web")
                .build()
        ).execute()
            .use { response ->
                if (!response.isSuccessful) LoginException.NetworkException(
                    response.request.url.toString(),
                    response.code
                ).left()
                else response.body?.let(ResponseBody::string)?.right()
                    ?: LoginException.NoAuthorizeResponse.left()
            }

    private fun findCode(authorizeHtmlResponse: String): Either<LoginException, String> =
        codeRegex.find(authorizeHtmlResponse)?.let { it.groups[1]?.value }?.right()
            ?: LoginException.NoCodeFound.left()

    private fun findState(authorizeHtmlResponse: String): Either<LoginException, String> =
        stateRegex.find(authorizeHtmlResponse)?.let { it.groups[1]?.value }?.right()
            ?: LoginException.NoStateFound.left()

    // Should rewrite to non-blocking suspend function using `suspendCoroutine`
    private fun logInCallback(state: String, code: String): Either<LoginException, Unit> =
        client.newCall(
            Request.Builder()
                .url("https://vtm.be/vtmgo/login-callback")
                .post(
                    FormBody.Builder()
                        .add("state", state)
                        .add("code", code)
                        .build()
                )
                .build()
        ).execute()
            .use {
                if (!it.isSuccessful) LoginException.NetworkException(
                    it.request.url.toString(),
                    it.code
                ).left()
                else Unit.right()
            }

    private fun getJWT(): Either<LoginException, JWT> =
        vtmCookieJar.getCookieValue(COOKIE_LFVP_AUTH)?.let(::JWT)
            .rightIfNotNull { LoginException.MissingCookieValue(COOKIE_LFVP_AUTH) }
}

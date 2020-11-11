package be.tapped.vtmgo.authentication

import arrow.core.Either
import arrow.core.left
import arrow.core.rightIfNotNull
import com.techvein.okhttp3.logging.CurlHttpLoggingInterceptor
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

inline class JWT(val token: String)
sealed class LoginException {
    data class NetworkException(val url: String, val code: Int) : LoginException()
    data class MissingCookieValue(val cookieValue: String) : LoginException()
    object NoAuthorizeResponse : LoginException()
    object NoCodeFound : LoginException()
    object NoStateFound : LoginException()
    data class Unknown(val throwable: Throwable) : LoginException()
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
            .addNetworkInterceptor(CurlHttpLoggingInterceptor { message -> println("$message\n\r") })
            .cookieJar(vtmCookieJar)
            .build()

    private val codeRegex = Regex("name=\"code\" value=\"([^\"]+)")
    private val stateRegex = Regex("name=\"state\" value=\"([^\"]+)")

    /**
     * https://github.com/add-ons/plugin.video.vtm.go/wiki/Authentication-API
     */
    fun login(userName: String, password: String): Either<LoginException, JWT> =
        runCatching {
            // Aanmelden
            client.newCall(
                Request.Builder()
                    .get()
                    .url("https://vtm.be/vtmgo/aanmelden?redirectUrl=https://vtm.be/vtmgo")
                    .build()
            ).execute()
                .use {
                    if (!it.isSuccessful) {
                        return LoginException.NetworkException(it.request.url.toString(), it.code)
                            .left()
                    }
                }

            if (vtmCookieJar.getCookieValue(COOKIE_LFVP_AUTH_STATE) == null) {
                return LoginException.MissingCookieValue(COOKIE_LFVP_AUTH_STATE).left()
            }
            if (vtmCookieJar.getCookieValue(COOKIE_X_OIDCP_DEBUGID) == null) {
                return LoginException.MissingCookieValue(COOKIE_X_OIDCP_DEBUGID).left()
            }
            if (vtmCookieJar.getCookieValue(COOKIE_X_OIDCP_TICKET) == null) {
                return LoginException.MissingCookieValue(COOKIE_X_OIDCP_TICKET).left()
            }

            // Login
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
                    if (!it.isSuccessful) {
                        return LoginException.NetworkException(it.request.url.toString(), it.code)
                            .left()
                    }
                }

            // Authorize
            val authorizeHtmlResponse = client.newCall(
                Request.Builder()
                    .get()
                    .url("https://login2.vtm.be/authorize/continue?client_id=vtm-go-web")
                    .build()
            ).execute()
                .use {
                    if (!it.isSuccessful) {
                        return LoginException.NetworkException(it.request.url.toString(), it.code)
                            .left()
                    }

                    if (it.body == null) {
                        return LoginException.NoAuthorizeResponse.left()
                    }

                    it.body!!.string()
                }

            val code = codeRegex.find(authorizeHtmlResponse)?.let { it.groups[1]?.value }
                ?: return LoginException.NoCodeFound.left()
            val state = stateRegex.find(authorizeHtmlResponse)?.let { it.groups[1]?.value }
                ?: return LoginException.NoStateFound.left()

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
                    if (!it.isSuccessful) {
                        return LoginException.NetworkException(it.request.url.toString(), it.code)
                            .left()
                    }
                }

            return vtmCookieJar.getCookieValue(COOKIE_LFVP_AUTH)?.let(::JWT)
                .rightIfNotNull { LoginException.MissingCookieValue(COOKIE_LFVP_AUTH) }
        }.getOrElse { LoginException.Unknown(it).left() }
}

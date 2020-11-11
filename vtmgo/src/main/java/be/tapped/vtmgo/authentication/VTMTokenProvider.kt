package be.tapped.vtmgo.authentication

import com.techvein.okhttp3.logging.CurlHttpLoggingInterceptor
import okhttp3.*
import okhttp3.internal.cookieToString
import java.util.*

class VTMCookieJar : CookieJar {
    private val cookieCache: MutableMap<HttpUrl, List<Cookie>> = mutableMapOf()

    private val defaultAuthIdCookie =
        Cookie.Builder()
            .name("authId")
            .value(UUID.randomUUID().toString())
            .domain("*")
            .build()

    override fun loadForRequest(url: HttpUrl): List<Cookie> =
        cookieCache.entries
            .filter { it.key.host == url.host }
            .flatMap(MutableMap.MutableEntry<HttpUrl, List<Cookie>>::value) + listOf(
            defaultAuthIdCookie
        )

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieCache[url] = cookies
    }

    fun getKeyByName(name: String) = cookieCache.values.flatten().firstOrNull { it.name == name }

    override fun toString(): String = "$cookieCache"
}

class VTMTokenProvider(private val vtmCookieJar: VTMCookieJar = VTMCookieJar()) {
    private val client =
        OkHttpClient.Builder()
            .addNetworkInterceptor(CurlHttpLoggingInterceptor { message -> println("$message\n\r") })
            .cookieJar(vtmCookieJar)
            .build()

    private val defaultHeaders = Headers.headersOf(
        "x-app-version", "8",
        "x-persgroep-mobile-app", "true",
        "x-persgroep-os", "android",
        "x-persgroep-os-version", "23",
    )

    fun login(userName: String, password: String) {
        val aanmeldenResponse = client.newCall(
            Request.Builder()
                .get()
                .url("https://vtm.be/vtmgo/aanmelden?redirectUrl=https://vtm.be/vtmgo")
                .build()
        ).execute()
        val state = aanmeldenResponse.priorResponse!!.request.url.queryParameter("state")!!

        client
            .newBuilder()
            .followRedirects(false)
            .build()
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

        val authorizeResponse = client.newCall(
            Request.Builder()
                .get()
                .url("https://login2.vtm.be/authorize/continue?client_id=vtm-go-web")
                .build()
        ).execute()

        val authorizeHtmlResponse = authorizeResponse.body!!.string()
        val code =
            Regex("name=\"code\" value=\"([^\"]+)").find(authorizeHtmlResponse)!!.groups[1]!!.value

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

        // No JWT Token received in cookies?
        val lfvpAuth = vtmCookieJar.getKeyByName("lfvp_auth")
        println("lfvpAuth=$lfvpAuth")
    }
}

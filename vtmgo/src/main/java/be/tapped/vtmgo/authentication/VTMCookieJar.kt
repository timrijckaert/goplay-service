package be.tapped.vtmgo.authentication

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.*

class VTMCookieJar : CookieJar {
    private val cookieCache: MutableMap<HttpUrl, List<Cookie>> = mutableMapOf()

    private val defaultAuthIdCookie =
        Cookie.Builder()
            .name("authId")
            .value(UUID.randomUUID().toString())
            .domain("*")
            .build()

    private val fullCookieList: List<Cookie>
        get() = cookieCache.values.flatten()

    override fun loadForRequest(url: HttpUrl): List<Cookie> =
        cookieCache.entries
            .filter { it.key.host == url.host }
            .flatMap(MutableMap.MutableEntry<HttpUrl, List<Cookie>>::value) + listOf(
            defaultAuthIdCookie
        )

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieCache[url] = cookies
    }

    fun getKeyByName(name: String) = fullCookieList.firstOrNull { it.name == name }

    override fun toString(): String = fullCookieList.joinToString("\r\n")
}

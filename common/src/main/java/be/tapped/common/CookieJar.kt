package be.tapped.common

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

interface ReadOnlyCookieJar : CookieJar {
    operator fun get(name: String): String?
}

/**
 * Simply saves all incoming cookies.
 * Matching cookies are returned based on the host name of the requested host.
 */
class DefaultCookieJar : ReadOnlyCookieJar {
    private val cookieCache: MutableMap<HttpUrl, List<Cookie>> = mutableMapOf()

    private val fullCookieList: List<Cookie>
        get() = cookieCache.values.flatten()

    override fun loadForRequest(url: HttpUrl): List<Cookie> =
        cookieCache.entries
            .filter { it.key.host == url.host }
            .flatMap(MutableMap.MutableEntry<HttpUrl, List<Cookie>>::value)
            .toSet()
            .toList()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieCache[url] = cookies
    }

    override operator fun get(name: String): String? = fullCookieList.firstOrNull { it.name == name }?.value

    override fun toString(): String = fullCookieList.joinToString("\r\n")
}

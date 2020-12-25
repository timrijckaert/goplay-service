package be.tapped.common.internal

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

public interface ReadOnlyCookieJar : CookieJar {
    public operator fun get(name: String): String?
}

/**
 * Simply saves all incoming cookies.
 * Matching cookies are returned based on the host name of the requested host.
 */
@InterModuleUseOnly
public class DefaultCookieJar : ReadOnlyCookieJar {
    private val cookieCache: MutableList<Cookie> = mutableListOf()

    override fun loadForRequest(url: HttpUrl): List<Cookie> =
        cookieCache
            .filter { it.matches(url) }
            .fold(mutableListOf<Cookie>()) { cookieAcc, cookie ->
                cookieAcc.removeAll { it.name == cookie.name }
                cookieAcc.add(cookie)
                cookieAcc
            }
            .toList()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieCache += cookies
    }

    override operator fun get(name: String): String? = cookieCache.firstOrNull { it.name == name }?.value

    override fun toString(): String = cookieCache.joinToString("\r\n")
}

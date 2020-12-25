package be.tapped.common.internal

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.*

public interface ReadOnlyCookieJar : CookieJar {
    public operator fun get(name: String): String?
}

/**
 * Simply saves all incoming cookies.
 * Matching cookies are returned based on the host name of the requested host.
 */
@InterModuleUseOnly
public class DefaultCookieJar(private val maxCachedCookies: Int = MAX_COOKIES) : ReadOnlyCookieJar {

    private companion object {
        private const val MAX_COOKIES: Int = 100
    }

    private val c by lazy {
        object : LinkedHashMap<HttpUrl, List<Cookie>>() {
            override fun removeEldestEntry(p0: MutableMap.MutableEntry<HttpUrl, List<Cookie>>?): Boolean {
                return this.size > maxCachedCookies;
            }
        }
    }

    private val cookieCache: MutableMap<HttpUrl, List<Cookie>> = Collections.synchronizedMap(c)

    internal val fullCookieList: List<Cookie>
        get() = cookieCache.values.flatten()

    override fun loadForRequest(url: HttpUrl): List<Cookie> =
        fullCookieList
            .filter { it.matches(url) }
            .fold(mutableListOf<Cookie>()) { cookieAcc, cookie ->
                cookieAcc.removeAll { it.name == cookie.name }
                cookieAcc.add(cookie)
                cookieAcc
            }
            .toList()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieCache[url] = cookies
    }

    override operator fun get(name: String): String? = fullCookieList.firstOrNull { it.name == name }?.value

    override fun toString(): String = fullCookieList.joinToString("\r\n")
}

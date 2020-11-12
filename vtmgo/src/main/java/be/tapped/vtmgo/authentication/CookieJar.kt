package be.tapped.vtmgo.authentication

import be.tapped.vtmgo.common.DefaultCookieJar
import be.tapped.vtmgo.common.ReadOnlyCookieJar
import okhttp3.Cookie
import okhttp3.HttpUrl
import java.util.*

internal class CookieJar(private val delegatingCookieJar: ReadOnlyCookieJar = DefaultCookieJar) : ReadOnlyCookieJar by delegatingCookieJar {

    private val defaultAuthIdCookie =
        Cookie.Builder()
            .name("authId")
            .value(UUID.randomUUID().toString())
            .domain("*")
            .build()

    override fun loadForRequest(url: HttpUrl): List<Cookie> =
        delegatingCookieJar.loadForRequest(url) + listOf(defaultAuthIdCookie)
}

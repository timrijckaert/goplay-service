package be.tapped.common.internal

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSingleElement
import okhttp3.Cookie
import okhttp3.HttpUrl

public class DefaultCookieJarTest : BehaviorSpec({
    given("A default cookie jar") {
        val sut = DefaultCookieJar()
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("host")
            .build()

        val buildCookie: (Int) -> Cookie = {
            Cookie.Builder()
                .domain("host")
                .name("name")
                .value("value$it")
                .build()
        }
        val cookies = (0..10).map { buildCookie(it) }

        sut.saveFromResponse(url, cookies)
        `when`("retrieving cookies for request") {
            val actualCookies = sut.loadForRequest(url)
            then("it should have de-duplicated the Cookie values") {
                actualCookies shouldHaveSingleElement buildCookie(10)
            }
        }
    }
})

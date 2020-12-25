package be.tapped.common.internal

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSingleElement
import okhttp3.Cookie
import okhttp3.HttpUrl

public class DefaultCookieJarTest : BehaviorSpec({
    val buildCookie: (Int) -> Cookie = {
        Cookie.Builder()
            .domain("host")
            .name("name")
            .value("value$it")
            .build()
    }

    val url = HttpUrl.Builder()
        .scheme("https")
        .host("host")
        .build()

    given("A default cookie jar") {
        val sut = DefaultCookieJar()
        and("it has added multiple cookies with the sample name") {
            val cookies = (0..10).map { buildCookie(it) }

            sut.saveFromResponse(url, cookies)
            `when`("retrieving cookies for request") {
                val actualCookies = sut.loadForRequest(url)
                then("it should have de-duplicated the Cookie values") {
                    actualCookies shouldHaveSingleElement buildCookie(10)
                }
            }
        }
    }

    given("a default cookie jar that is completely full") {
        val sut = DefaultCookieJar(1)

        sut.saveFromResponse(url, listOf(buildCookie(0)))

        `when`("adding a new cookie") {
            sut.saveFromResponse(url, listOf(buildCookie(1)))
            then("it should have removed the oldest item") {
                sut.fullCookieList.shouldHaveSingleElement(buildCookie(1))
            }
        }
    }
})

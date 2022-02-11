package be.tapped.goplay

import be.tapped.goplay.content.httpClient
import be.tapped.goplay.epg.EpgRepo
import be.tapped.goplay.epg.httpEpgRepo
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.ShouldSpec
import io.ktor.util.date.*
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone.Companion
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import java.util.*

internal class EpgRepoTest : ShouldSpec({
    GoPlayBrand.values().forEach { brand ->
        val sut = httpEpgRepo(httpClient)

        should("be able to fetch the day before yesterday's EPG for $brand") {
            val dayBeforeYesterday = now() - DatePeriod(days = 2)
            sut.epg(brand, dayBeforeYesterday).shouldBeRight()
        }

        should("be able to fetch yesterday's EPG for $brand") {
            val yesterday = now() - DatePeriod(days = 1)
            sut.epg(brand, yesterday).shouldBeRight()
        }

        should("be able to fetch today's EPG for $brand") {
            sut.epg(brand, now()).shouldBeRight()
        }

        (1..5).forEach { positiveDayOffset ->
            should("be able to fetch today's + $positiveDayOffset EPG for $brand") {
                val todayPlusOffset = now() + DatePeriod(days = positiveDayOffset)
                sut.epg(brand, todayPlusOffset).shouldBeRight()
            }
        }
    }
})

private fun now() = Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.UTC).date

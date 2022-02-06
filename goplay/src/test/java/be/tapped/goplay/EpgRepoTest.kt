package be.tapped.goplay

import be.tapped.goplay.content.httpClient
import be.tapped.goplay.epg.EpgRepo
import be.tapped.goplay.epg.httpEpgRepo
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.ShouldSpec
import java.util.Calendar
import java.util.TimeZone

internal class EpgRepoTest : ShouldSpec({
    EpgRepo.Brand.values().forEach {
        // TODO if tests are going to be MPP we need a way to fetch the date without a dep on java.util.Calendar
        val today = Calendar.getInstance(TimeZone.getTimeZone("Europe/Brussels"))
        val sut = httpEpgRepo(httpClient)

        should("be able to fetch the day before yesterday's EPG for $it") {
            val dayBeforeYesterday = today.apply { add(Calendar.DAY_OF_MONTH, -2) }
            sut.epg(it, dayBeforeYesterday.toDate()).shouldBeRight()
        }

        should("be able to fetch yesterday's EPG for $it") {
            val yesterday = today.apply { add(Calendar.DAY_OF_MONTH, -1) }
            sut.epg(it, yesterday.toDate()).shouldBeRight()
        }

        should("be able to fetch today's EPG for $it") {
            sut.epg(it, today.toDate()).shouldBeRight()
        }

        should("be able to fetch tomorrow's EPG for $it") {
            val tomorrow = today.apply { add(Calendar.DAY_OF_MONTH, 1) }
            sut.epg(it, tomorrow.toDate()).shouldBeRight()
        }

        should("be able to fetch the day after tomorrow's EPG for $it") {
            val dayAfterTomorrow = today.apply { add(Calendar.DAY_OF_MONTH, 2) }
            sut.epg(it, dayAfterTomorrow.toDate()).shouldBeRight()
        }
    }
})

private fun Calendar.toDate(): EpgRepo.Date {
    val year = get(Calendar.YEAR)
    val month = get(Calendar.MONTH) + 1
    val dayOfTheMonth = get(Calendar.DAY_OF_MONTH)
    return EpgRepo.Date(year, month, dayOfTheMonth)
}

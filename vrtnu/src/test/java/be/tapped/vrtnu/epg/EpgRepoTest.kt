package be.tapped.vrtnu.epg

import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import java.util.*

public class EpgRepoTest : StringSpec() {
    private val epg = HttpEpgRepo()
    private val today get() = Calendar.getInstance()

    init {
        "should be able to retrieve EPG for today" {
            epg.epg(today).shouldBeRight()
        }

        (1..30).forEach { days ->
            "should be able to retrieve EPG for $days days back in time" {
                val dateInThePast = today.apply {
                    add(Calendar.DAY_OF_MONTH, -days)
                }
                epg.epg(dateInThePast).shouldBeRight()
            }
        }

        (1..14).forEach { days ->
            "should be able to retrieve EPG for $days days in the future" {
                val dateInThePast = today.apply {
                    add(Calendar.DAY_OF_MONTH, days)
                }
                epg.epg(dateInThePast).shouldBeRight()
            }
        }
    }
}

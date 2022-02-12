package be.tapped.goplay.epg

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import be.tapped.goplay.ApiResponse
import be.tapped.goplay.siteUrl
import be.tapped.goplay.safeGet
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate

internal fun interface EpgRepo {
    suspend fun epg(brand: GoPlayBrand, date: LocalDate): Either<ApiResponse.Failure, ApiResponse.Success.ProgramGuide>
}

// curl -X GET "https://www.goplay.be/api/epg/vier/2020-12-13"
// curl -X GET "https://www.goplay.be/api/epg/vijf/2020-12-13"
// curl -X GET "https://www.goplay.be/api/epg/zes/2020-12-13"
// curl -X GET "https://www.goplay.be/api/epg/zeven/2020-12-13"
internal fun httpEpgRepo(client: HttpClient): EpgRepo =
    EpgRepo { goPlayBrand, date ->
        val year = date.year
        val month = date.month.value
        val dayOfMonth = date.dayOfMonth
        val brandPath =
            when (goPlayBrand) {
                GoPlayBrand.Play4 -> "vier"
                GoPlayBrand.Play5 -> "vijf"
                GoPlayBrand.Play6 -> "zes"
                GoPlayBrand.Play7 -> "zeven"
            }
        val monthStr = if (month < 10) "0${month}" else "$month"
        val dayStr = if (dayOfMonth < 10) "0${dayOfMonth}" else "$dayOfMonth"
        val url = "$siteUrl/api/epg/$brandPath/${year}-$monthStr-$dayStr"

        withContext(Dispatchers.IO) {
            either {
                val epg = client.safeGet<List<EpgProgram>>(url).bind()
                if (epg.isEmpty()) {
                    ApiResponse.Failure.Epg.NoEpgDataFor(date).left()
                } else {
                    ApiResponse.Success.ProgramGuide(epg).right()
                }.bind()
            }
        }
    }

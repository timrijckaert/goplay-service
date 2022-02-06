package be.tapped.goplay.epg

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import be.tapped.goplay.ApiResponse
import be.tapped.goplay.content.siteUrl
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

public fun interface EpgRepo {

    public enum class Brand {
        VIER,
        VIJF,
        ZES;
    }

    /**
     * @param month 0 based. 0 being january
     * @param dayOfTheMonth 1-31 (Arrow Analysis?)
     */
    public data class Date(val year: Int, val month: Int, val dayOfTheMonth: Int)

    public suspend fun epg(
        brand: Brand,
        date: Date,
    ): Either<ApiResponse.Failure, ApiResponse.Success.ProgramGuide>
}

// curl -X GET "https://www.goplay.be/api/epg/vier/2020-12-13"
// curl -X GET "https://www.goplay.be/api/epg/vijf/2020-12-13"
// curl -X GET "https://www.goplay.be/api/epg/zes/2020-12-13"
internal fun httpEpgRepo(client: HttpClient): EpgRepo =
    EpgRepo { brand: EpgRepo.Brand, date: EpgRepo.Date ->
        val (year, month, day) = date
        val brandPath = when (brand) {
            EpgRepo.Brand.VIER -> "vier"
            EpgRepo.Brand.VIJF -> "vijf"
            EpgRepo.Brand.ZES -> "zes"
        }
        val monthStr = if (month < 10) "0${month}" else "$month"
        val dayStr = if (day < 10) "0${day}" else "$day"
        val url = "$siteUrl/api/epg/$brandPath/${year}-$monthStr-$dayStr"

        withContext(Dispatchers.IO) {
            either {
                val epg = client.get<List<EpgProgram>>(url)
                if (epg.isEmpty()) {
                    ApiResponse.Failure.Epg.NoEpgDataFor(date).left()
                } else {
                    ApiResponse.Success.ProgramGuide(epg).right()
                }.bind()
            }
        }
    }

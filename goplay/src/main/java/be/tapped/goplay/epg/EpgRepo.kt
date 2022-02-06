package be.tapped.goplay.epg

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import be.tapped.common.internal.executeAsync
import be.tapped.goplay.ApiResponse
import be.tapped.goplay.common.goPlayApiDefaultOkHttpClient
import be.tapped.goplay.common.jsonSerializer
import be.tapped.goplay.common.ktorClient
import be.tapped.goplay.common.safeBodyString
import be.tapped.goplay.common.siteUrl
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

public class JsonEpgParser(private val serializer: Json) {
    public fun parse(json: String): Either<ApiResponse.Failure, List<EpgProgram>> =
        Either.catch { serializer.decodeFromString<List<EpgProgram>>(json) }.mapLeft(ApiResponse.Failure::JsonParsingException)
}

public sealed interface EpgRepo {

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

public class HttpEpgRepo(
    private val client: OkHttpClient = goPlayApiDefaultOkHttpClient,
    private val _client: HttpClient = ktorClient,
    private val jsonEpgParser: JsonEpgParser = JsonEpgParser(jsonSerializer),
) : EpgRepo {

    // curl -X GET "https://www.goplay.be/api/epg/vier/2020-12-13"
    // curl -X GET "https://www.goplay.be/api/epg/vijf/2020-12-13"
    // curl -X GET "https://www.goplay.be/api/epg/zes/2020-12-13"
    override suspend fun epg(brand: EpgRepo.Brand, date: EpgRepo.Date): Either<ApiResponse.Failure, ApiResponse.Success.ProgramGuide> =
        withContext(Dispatchers.IO) {
            val response = client.executeAsync(Request.Builder().get().url(constructUrl(brand, date)).build())
            either {
                val json = response.safeBodyString().bind()
                val epg = jsonEpgParser.parse(json).bind()

                if (epg.isEmpty()) {
                    ApiResponse.Failure.Epg.NoEpgDataFor(date).left()
                } else {
                    ApiResponse.Success.ProgramGuide(epg).right()
                }.bind()
            }
        }

    private fun constructUrl(
        brand: EpgRepo.Brand,
        date: EpgRepo.Date,
    ): String {
        val (year, month, day) = date
        val brandPath = when (brand) {
            EpgRepo.Brand.VIER -> "vier"
            EpgRepo.Brand.VIJF -> "vijf"
            EpgRepo.Brand.ZES -> "zes"
        }
        val monthStr = if (month < 10) "0${month}" else "$month"
        val dayStr = if (day < 10) "0${day}" else "$day"
        return "$siteUrl/api/epg/$brandPath/${year}-$monthStr-$dayStr"
    }
}

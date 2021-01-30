package be.tapped.goplay.epg

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import be.tapped.common.internal.executeAsync
import be.tapped.goplay.ApiResponse
import be.tapped.goplay.common.safeBodyString
import be.tapped.goplay.common.goPlayApiDefaultOkHttpClient
import be.tapped.goplay.common.siteUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.util.*

public class JsonEpgParser {
    public fun parse(json: String): Either<ApiResponse.Failure, List<EpgProgram>> = Either.catch {
        Json {
            isLenient = true
            ignoreUnknownKeys = true
        }.decodeFromString<List<EpgProgram>>(json)
    }.mapLeft(ApiResponse.Failure::JsonParsingException)
}

public interface EpgRepo {

    public enum class Brand {
        VIER,
        VIJF,
        ZES;
    }

    public suspend fun epg(brand: Brand, calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Brussels"))): Either<ApiResponse.Failure, ApiResponse.Success.ProgramGuide>
}

public class HttpEpgRepo(
        private val client: OkHttpClient = goPlayApiDefaultOkHttpClient,
        private val jsonEpgParser: JsonEpgParser = JsonEpgParser(),
        private val dateFormatter: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd"),
) : EpgRepo {

    // curl -X GET "https://www.goplay.be/api/epg/vier/2020-12-13"
    // curl -X GET "https://www.goplay.be/api/epg/vijf/2020-12-13"
    // curl -X GET "https://www.goplay.be/api/epg/zes/2020-12-13"
    override suspend fun epg(brand: EpgRepo.Brand, calendar: Calendar): Either<ApiResponse.Failure, ApiResponse.Success.ProgramGuide> = withContext(Dispatchers.IO) {
        val response = client.executeAsync(Request.Builder().get().url(constructUrl(brand, calendar)).build())
        either {
            val json = !response.safeBodyString()
            val epg = !jsonEpgParser.parse(json)

            !if (epg.isEmpty()) {
                ApiResponse.Failure.Epg.NoEpgDataFor(calendar).left()
            } else {
                ApiResponse.Success.ProgramGuide(epg).right()
            }
        }
    }

    private fun constructUrl(brand: EpgRepo.Brand, calendar: Calendar): String {
        val brandPath = when(brand) {
            EpgRepo.Brand.VIER -> "vier"
            EpgRepo.Brand.VIJF -> "vijf"
            EpgRepo.Brand.ZES -> "zes"
        }
        return "$siteUrl/api/epg/$brandPath/${dateFormatter.format(calendar.time)}"
    }
}

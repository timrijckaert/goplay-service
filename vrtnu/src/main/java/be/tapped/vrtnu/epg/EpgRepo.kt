package be.tapped.vrtnu.epg

import arrow.core.Either
import arrow.core.computations.either
import be.tapped.common.executeAsync
import be.tapped.common.validateResponse
import be.tapped.vrtnu.ApiResponse
import be.tapped.vrtnu.common.defaultOkHttpClient
import be.tapped.vrtnu.ApiResponse.Failure.JsonParsingException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.*

class JsonEpgParser {
    suspend fun parse(json: String): Either<ApiResponse.Failure, Epg> =
        Either.catch { Json.decodeFromString<Epg>(json) }.mapLeft(::JsonParsingException)
}

interface EpgRepo {
    suspend fun epg(calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Brussels"))): Either<ApiResponse.Failure, ApiResponse.Success.ProgramGuide>
}

class HttpEpgRepo(
    private val client: OkHttpClient = defaultOkHttpClient,
    private val jsonEpgParser: JsonEpgParser = JsonEpgParser(),
) : EpgRepo {

    // curl 'https://www.vrt.be/bin/epg/schedule.2020-11-21.json'
    private fun constructUrl(dayOfTheMonth: Int, month: Int, year: Int): HttpUrl {
        return HttpUrl.Builder()
            .scheme("https")
            .host("vrt.be")
            .addPathSegments("bin/epg")
            .addPathSegment("schedule.$year-$month-$dayOfTheMonth.json")
            .build()
    }

    override suspend fun epg(calendar: Calendar): Either<ApiResponse.Failure, ApiResponse.Success.ProgramGuide> {
        val year: Int = calendar.get(Calendar.YEAR)
        // Note that months are 0 based. 0 -> january, 11 -> december
        val month: Int = calendar.get(Calendar.MONTH) + 1
        val dayOfTheMonth: Int = calendar.get(Calendar.DAY_OF_MONTH)

        return withContext(Dispatchers.IO) {
            val epgResponse = client.executeAsync(
                Request.Builder()
                    .get()
                    .url(constructUrl(dayOfTheMonth, month, year))
                    .build()
            )

            either {
                !epgResponse.validateResponse { ApiResponse.Failure.NetworkFailure(epgResponse.code, epgResponse.request) }
                val epgJson = !Either.fromNullable(epgResponse.body).mapLeft { ApiResponse.Failure.EmptyJson }
                ApiResponse.Success.ProgramGuide(!jsonEpgParser.parse(epgJson.string()))
            }
        }
    }
}

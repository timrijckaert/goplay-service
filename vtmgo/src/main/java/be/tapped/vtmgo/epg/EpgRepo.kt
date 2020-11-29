package be.tapped.vtmgo.epg

import arrow.core.Either
import arrow.core.computations.either
import be.tapped.common.executeAsync
import be.tapped.vtmgo.ApiResponse
import be.tapped.vtmgo.ApiResponse.Failure.JsonParsingException
import be.tapped.vtmgo.common.defaultOkHttpClient
import be.tapped.vtmgo.common.safeBodyString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
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
    private val dateFormatter: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
) : EpgRepo {

    // curl -X GET \
    // -H "Host:vtm.be" \
    // -H "Connection:Keep-Alive" \
    // -H "Accept-Encoding:gzip" \
    // -H "Cookie: authId=<uuid4>" \
    // -H "User-Agent:okhttp/4.9.0" "https://vtm.be/tv-gids/api/v2/broadcasts/<year>-<monthIndex>-<dayOfWeek>"
    override suspend fun epg(calendar: Calendar): Either<ApiResponse.Failure, ApiResponse.Success.ProgramGuide> =
        withContext(Dispatchers.IO) {
            val epgResponse = client.executeAsync(
                Request.Builder()
                    .get()
                    .url(constructUrl(calendar))
                    .build()
            )

            either {
                ApiResponse.Success.ProgramGuide(!jsonEpgParser.parse(!epgResponse.safeBodyString()))
            }
        }

    private fun constructUrl(calendar: Calendar): String = "https://vtm.be/tv-gids/api/v2/broadcasts/${dateFormatter.format(calendar.time)}"
}

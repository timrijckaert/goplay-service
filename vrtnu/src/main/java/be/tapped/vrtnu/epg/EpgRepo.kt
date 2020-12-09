package be.tapped.vrtnu.epg

import arrow.core.Either
import arrow.core.computations.either
import be.tapped.common.internal.executeAsync
import be.tapped.vrtnu.ApiResponse
import be.tapped.vrtnu.ApiResponse.Failure.JsonParsingException
import be.tapped.vrtnu.common.defaultOkHttpClient
import be.tapped.vrtnu.common.safeBodyString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.util.*

public class JsonEpgParser {
    public suspend fun parse(json: String): Either<ApiResponse.Failure, Epg> =
        Either.catch { Json.decodeFromString<Epg>(json) }.mapLeft(::JsonParsingException)
}

public interface EpgRepo {
    public suspend fun epg(calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Brussels"))): Either<ApiResponse.Failure, ApiResponse.Success.ProgramGuide>
}

public class HttpEpgRepo(
    private val client: OkHttpClient = defaultOkHttpClient,
    private val jsonEpgParser: JsonEpgParser = JsonEpgParser(),
    private val dateFormatter: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd"),
) : EpgRepo {

    // curl 'https://www.vrt.be/bin/epg/schedule.2020-11-21.json'
    private fun constructUrl(calendar: Calendar): HttpUrl {
        return HttpUrl.Builder()
            .scheme("https")
            .host("vrt.be")
            .addPathSegments("bin/epg")
            .addPathSegment("schedule.${dateFormatter.format(calendar.time)}.json")
            .build()
    }

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
}

package be.tapped.vier.epg

import arrow.core.Either
import arrow.core.computations.either
import be.tapped.common.internal.executeAsync
import be.tapped.vier.ApiResponse
import be.tapped.vier.common.safeBodyString
import be.tapped.vier.common.vierApiDefaultOkHttpClient
import be.tapped.vier.common.vierUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.util.*

public class JsonEpgParser {
    public suspend fun parse(json: String): Either<ApiResponse.Failure, List<EpgProgram>> =
        Either
            .catch {
                Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                }.decodeFromString<List<EpgProgram>>(json)
            }
            .mapLeft(ApiResponse.Failure::JsonParsingException)
}

public interface EpgRepo {
    public suspend fun epg(calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Brussels"))): Either<ApiResponse.Failure, ApiResponse.Success.ProgramGuide>
}

public class HttpEpgRepo(
    private val client: OkHttpClient = vierApiDefaultOkHttpClient,
    private val jsonEpgParser: JsonEpgParser = JsonEpgParser(),
    private val dateFormatter: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd"),
) : EpgRepo {

    // curl -X GET "https://www.vier.be/api/epg/2020-12-13"
    override suspend fun epg(calendar: Calendar): Either<ApiResponse.Failure, ApiResponse.Success.ProgramGuide> =
        withContext(Dispatchers.IO) {
            val response = client.executeAsync(
                Request.Builder()
                    .get()
                    .url("$vierUrl/api/epg/${dateFormatter.format(calendar.time)}")
                    .build()
            )

            either {
                val epg = !jsonEpgParser.parse(!response.safeBodyString())
                ApiResponse.Success.ProgramGuide(
                    !Either.conditionally(
                        epg.isEmpty(),
                        ifTrue = { epg },
                        ifFalse = { ApiResponse.Failure.Epg.NoEpgDataFor(calendar) }
                    )
                )
            }
        }
}

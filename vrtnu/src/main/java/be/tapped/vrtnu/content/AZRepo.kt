package be.tapped.vrtnu.content

import arrow.core.Either
import arrow.core.computations.either
import be.tapped.vrtnu.content.ApiResponse.Failure.JsonParsingException
import be.tapped.vtmgo.common.executeAsync
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

internal class JsonAZProgramParser {
    suspend fun parse(json: String): Either<ApiResponse.Failure, List<Program>> =
        Either.catch { Json.decodeFromString<List<Program>>(json) }.mapLeft(::JsonParsingException)
}

interface AZRepo {
    suspend fun fetchAZPrograms(): Either<ApiResponse.Failure, ApiResponse.Success.AlphabeticPrograms>
}

internal class HttpAZRepo(
    private val client: OkHttpClient,
    private val jsonAZProgramParser: JsonAZProgramParser,
) : AZRepo {

    companion object {
        private const val AZ = "https://vrtnu-api.vrt.be/suggest?facets[transcodingStatus]=AVAILABLE"
    }

    override suspend fun fetchAZPrograms(): Either<ApiResponse.Failure, ApiResponse.Success.AlphabeticPrograms> {
        val azResponse = client.executeAsync(
            Request.Builder()
                .get()
                .url(AZ)
                .build()
        )

        return either {
            val rawAZJson = !Either.fromNullable(azResponse.body).mapLeft { ApiResponse.Failure.EmptyJson }
            ApiResponse.Success.AlphabeticPrograms(!jsonAZProgramParser.parse(rawAZJson.string()))
        }
    }
}

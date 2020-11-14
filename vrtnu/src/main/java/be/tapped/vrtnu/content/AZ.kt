package be.tapped.vrtnu.content

import arrow.core.Either
import arrow.core.computations.either
import be.tapped.vrtnu.content.ApiResponse.Failure.JsonParsingException
import be.tapped.vtmgo.common.executeAsync
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.double
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request

interface AZ {
    suspend fun fetchAZPrograms(): Either<ApiResponse.Failure, ApiResponse.Success.AlphabeticPrograms>
}

internal class JsonAZProgramParser {
    suspend fun parse(json: String): Either<ApiResponse.Failure, List<AZProgram>> =
        Either.catch {
            val jsonArr = Json.decodeFromString<JsonArray>(json)
            jsonArr.map {
                val programJson = it.jsonObject
                AZProgram(
                    title = programJson["title"]!!.jsonPrimitive.content,
                    type = programJson["type"]!!.jsonPrimitive.content,
                    episodeCount = programJson["episode_count"]!!.jsonPrimitive.int,
                    score = programJson["score"]!!.jsonPrimitive.double,
                    programUrl = programJson["programUrl"]!!.jsonPrimitive.content,
                    targetUrl = programJson["targetUrl"]!!.jsonPrimitive.content,
                    programName = programJson["programName"]!!.jsonPrimitive.content,
                    thumbnail = programJson["thumbnail"]!!.jsonPrimitive.content,
                    alternativeImage = programJson["alternativeImage"]!!.jsonPrimitive.content,
                    brands = programJson["brands"]!!.jsonArray.map { Brand(it.jsonPrimitive.content) },
                    description = programJson["description"]!!.jsonPrimitive.content,
                )
            }
        }.mapLeft(::JsonParsingException)
}

internal class HttpAZ(
    private val client: OkHttpClient,
    private val jsonAZProgramParser: JsonAZProgramParser,
) : AZ {

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

package be.tapped.vtmgo.content

import arrow.core.Either
import arrow.core.computations.either
import be.tapped.common.internal.executeAsync
import be.tapped.vtmgo.ApiResponse
import be.tapped.vtmgo.ApiResponse.Failure.JsonParsingException
import be.tapped.vtmgo.common.HeaderBuilder
import be.tapped.vtmgo.common.safeBodyString
import be.tapped.vtmgo.profile.JWT
import be.tapped.vtmgo.profile.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import okhttp3.OkHttpClient
import okhttp3.Request

internal class JsonProgramParser {
    suspend fun parse(json: String): Either<ApiResponse.Failure, Program> =
        Either.catch<Program> { Json.decodeFromJsonElement(Json.decodeFromString<JsonObject>(json)["program"]!!.jsonObject) }
            .mapLeft(::JsonParsingException)
}

public interface EpisodeRepo {
    public suspend fun fetchProgram(
        program: TargetResponse.Target.Program,
        jwt: JWT,
        profile: Profile,
    ): Either<ApiResponse.Failure, ApiResponse.Success.Content.Programs>
}

internal class HttpEpisodeRepo(
    private val client: OkHttpClient,
    private val baseContentHttpUrlBuilder: BaseContentHttpUrlBuilder,
    private val headerBuilder: HeaderBuilder,
    private val jsonProgramParser: JsonProgramParser,
) : EpisodeRepo {

    override suspend fun fetchProgram(
        program: TargetResponse.Target.Program,
        jwt: JWT,
        profile: Profile,
    ): Either<ApiResponse.Failure, ApiResponse.Success.Content.Programs> = withContext(Dispatchers.IO) {
        val response = client.executeAsync(
            Request.Builder().get().headers(headerBuilder.authenticationHeaders(jwt, profile)).url(constructUrl(profile, program)).build()
        )

        either {
            ApiResponse.Success.Content.Programs(!jsonProgramParser.parse(!response.safeBodyString()))
        }
    }

    private fun constructUrl(profile: Profile, program: TargetResponse.Target.Program) =
        baseContentHttpUrlBuilder.constructBaseContentUrl(profile.product).addPathSegment("programs").addPathSegment(program.id).build()
}

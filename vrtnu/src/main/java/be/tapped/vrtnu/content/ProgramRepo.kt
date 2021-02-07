package be.tapped.vrtnu.content

import arrow.core.Either
import arrow.core.computations.either
import be.tapped.common.internal.executeAsync
import be.tapped.vrtnu.ApiResponse
import be.tapped.vrtnu.ApiResponse.Failure.JsonParsingException
import be.tapped.vrtnu.common.safeBodyString
import be.tapped.vrtnu.content.ElasticSearchQueryBuilder.applySearchQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

internal class ProgramSanitizer(private val urlPrefixMapper: UrlPrefixMapper) {

    internal fun sanitizeProgram(program: Program): Program = program.copy(
        alternativeImage = urlPrefixMapper.toHttpsUrl(program.alternativeImage),
        programUrl = urlPrefixMapper.toHttpsUrl(program.programUrl),
        targetUrl = urlPrefixMapper.toHttpsUrl(program.targetUrl),
        thumbnail = urlPrefixMapper.toHttpsUrl(program.thumbnail),
    )
}

internal class JsonProgramParser(private val programSanitizer: ProgramSanitizer) {
    suspend fun parse(json: String): Either<ApiResponse.Failure, List<Program>> = Either.catch {
        Json.decodeFromString<List<Program>>(json).map(programSanitizer::sanitizeProgram)
    }.mapLeft(::JsonParsingException)
}

public sealed interface ProgramRepo {

    public suspend fun fetchAZPrograms(): Either<ApiResponse.Failure, ApiResponse.Success.Content.Programs>

    public suspend fun fetchProgramByName(programName: String): Either<ApiResponse.Failure, ApiResponse.Success.Content.SingleProgram>

}

internal class HttpProgramRepo(
    private val client: OkHttpClient,
    private val jsonProgramParser: JsonProgramParser,
) : ProgramRepo {

    override suspend fun fetchAZPrograms(): Either<ApiResponse.Failure, ApiResponse.Success.Content.Programs> = withContext(Dispatchers.IO) {
        either {
            val programsAZSorted = client.executeAsync(
                Request.Builder().get().url(!constructUrl(ElasticSearchQueryBuilder.SearchQuery(transcodingStatus = TranscodingStatus.AVAILABLE)))
                    .build()
            )

            ApiResponse.Success.Content.Programs(!jsonProgramParser.parse(!programsAZSorted.safeBodyString()))
        }
    }

    override suspend fun fetchProgramByName(programName: String): Either<ApiResponse.Failure, ApiResponse.Success.Content.SingleProgram> =
        withContext(Dispatchers.IO) {
            either {
                val fetchSingleProgram = client.executeAsync(
                    Request.Builder().get().url(
                        !constructUrl(
                            ElasticSearchQueryBuilder.SearchQuery(
                                transcodingStatus = TranscodingStatus.AVAILABLE, programName = programName, size = 1
                            )
                        )
                    ).build()
                )

                ApiResponse.Success.Content.SingleProgram((!jsonProgramParser.parse(!fetchSingleProgram.safeBodyString())).firstOrNull())
            }
        }

    private fun constructUrl(searchQuery: ElasticSearchQueryBuilder.SearchQuery) =
        HttpUrl.Builder().scheme("https").host("vrtnu-api.vrt.be").addPathSegment("suggest").applySearchQuery(searchQuery).map(HttpUrl.Builder::build)
}

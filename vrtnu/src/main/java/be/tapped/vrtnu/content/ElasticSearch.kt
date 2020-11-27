package be.tapped.vrtnu.content

import arrow.core.Either
import arrow.core.EitherPartialOf
import arrow.core.computations.either
import arrow.typeclasses.suspended.BindSyntax
import be.tapped.common.executeAsync
import be.tapped.common.validateResponse
import be.tapped.vrtnu.ApiResponse
import be.tapped.vrtnu.ApiResponse.Failure.JsonParsingException
import be.tapped.vrtnu.content.ElasticSearchQueryBuilder.applySearchQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

internal class JsonEpisodeParser {
    suspend fun parse(json: String): Either<ApiResponse.Failure, ElasticSearchResult<Episode>> =
        Either
            .catch { Json.decodeFromString<ElasticSearchResult<Episode>>(json) }
            .mapLeft(::JsonParsingException)
}

interface EpisodeRepo {

    fun episodes(searchQuery: ElasticSearchQueryBuilder.SearchQuery): Flow<Either<ApiResponse.Failure, ApiResponse.Success.Episodes>>

    fun episodesForProgram(program: Program): Flow<Either<ApiResponse.Failure, ApiResponse.Success.Episodes>> =
        episodes(ElasticSearchQueryBuilder.SearchQuery(programName = program.programName))

}

internal class HttpEpisodeRepo(
    private val client: OkHttpClient,
    private val jsonEpisodeParser: JsonEpisodeParser,
) : EpisodeRepo {

    override fun episodes(searchQuery: ElasticSearchQueryBuilder.SearchQuery): Flow<Either<ApiResponse.Failure, ApiResponse.Success.Episodes>> =
        unfoldFlow(searchQuery.pageIndex) { index ->
            withContext(Dispatchers.IO) {
                val episodeByCategoryResponse = client.executeAsync(
                    Request.Builder()
                        .get()
                        .url(constructUrl(searchQuery.copy(pageIndex = index)))
                        .build()
                )

                !episodeByCategoryResponse.validateResponse {
                    ApiResponse.Failure.NetworkFailure(
                        episodeByCategoryResponse.code,
                        episodeByCategoryResponse.request
                    )
                }
                val rawJson = !Either.fromNullable(episodeByCategoryResponse.body).mapLeft { ApiResponse.Failure.EmptyJson }
                val searchResultEpisodes = !jsonEpisodeParser.parse(rawJson.string())

                if (index > searchResultEpisodes.meta.pages.total) null
                else Pair(index + 1, ApiResponse.Success.Episodes(searchResultEpisodes.results))
            }
        }

    private fun constructUrl(searchQuery: ElasticSearchQueryBuilder.SearchQuery) =
        HttpUrl.Builder()
            .scheme("https")
            .host("vrtnu-api.vrt.be")
            .addPathSegment("search")
            .applySearchQuery(searchQuery)
            .build()

    private fun <A, B, E> unfoldFlow(initial: A, next: suspend BindSyntax<EitherPartialOf<E>>.(A) -> Pair<A, B>?): Flow<Either<E, B>> =
        flow {
            var initial: A? = initial
            val res: Either<E, Unit> = either {
                do {
                    val nextEither: Pair<A, B>? = next(this@either, initial!!)

                    initial = if (nextEither != null) {
                        emit(Either.Right(nextEither.second))
                        nextEither.first
                    } else null
                } while (initial != null)
            }
            when (res) {
                is Either.Left -> emit(res)
                is Either.Right -> Unit
            }
        }
}

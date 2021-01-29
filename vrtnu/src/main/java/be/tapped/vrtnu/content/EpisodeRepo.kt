package be.tapped.vrtnu.content

import arrow.core.Either
import arrow.core.computations.EitherEffect
import arrow.core.computations.either
import be.tapped.common.internal.executeAsync
import be.tapped.vrtnu.ApiResponse
import be.tapped.vrtnu.ApiResponse.Failure.JsonParsingException
import be.tapped.vrtnu.common.safeBodyString
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

internal class JsonEpisodeParser(private val urlPrefixMapper: UrlPrefixMapper) {
    suspend fun parse(json: String): Either<ApiResponse.Failure, ElasticSearchResult<Episode>> {
        return Either.catch { Json.decodeFromString<ElasticSearchResult<Episode>>(json) }.map { elasticSearch ->
            elasticSearch.copy(results = elasticSearch.results.map {
                it.copy(
                    programImageUrl = urlPrefixMapper.toHttpsUrl(it.programImageUrl),
                    videoThumbnailUrl = urlPrefixMapper.toHttpsUrl(it.videoThumbnailUrl),
                )
            })
        }.mapLeft(::JsonParsingException)
    }
}

public interface EpisodeRepo {

    public fun episodes(searchQuery: ElasticSearchQueryBuilder.SearchQuery): Flow<Either<ApiResponse.Failure, ApiResponse.Success.Content.Episodes>>

    public fun episodesForProgram(program: Program): Flow<Either<ApiResponse.Failure, ApiResponse.Success.Content.Episodes>> =
        episodes(ElasticSearchQueryBuilder.SearchQuery(programName = program.programName))

    public fun fetchMostRecent(): Flow<Either<ApiResponse.Failure, ApiResponse.Success.Content.Episodes>> = episodes(
        ElasticSearchQueryBuilder.SearchQuery(
            size = 25,
            custom = mapOf("allowedRegion" to "BE,WORLD", "brands" to "een,canvas,klara,mnm,radio1,radio2,sporza,stubru,vrtnws,vrtnu,vrtnxt"),
        )
    )
}

internal class HttpEpisodeRepo(
    private val client: OkHttpClient,
    private val jsonEpisodeParser: JsonEpisodeParser,
) : EpisodeRepo {

    override fun episodes(searchQuery: ElasticSearchQueryBuilder.SearchQuery): Flow<Either<ApiResponse.Failure, ApiResponse.Success.Content.Episodes>> =
        unfoldFlow(searchQuery.pageIndex) { index ->
            withContext(Dispatchers.IO) {
                val episodeByCategoryResponse =
                    client.executeAsync(Request.Builder().get().url(!constructUrl(searchQuery.copy(pageIndex = index))).build())

                val searchResultEpisodes = !jsonEpisodeParser.parse(!episodeByCategoryResponse.safeBodyString())
                if (index != searchQuery.pageIndex && index >= searchResultEpisodes.meta.pages.total) null
                else Pair(index + 1, ApiResponse.Success.Content.Episodes(searchResultEpisodes.results))
            }
        }

    private fun constructUrl(searchQuery: ElasticSearchQueryBuilder.SearchQuery) =
        HttpUrl.Builder().scheme("https").host("vrtnu-api.vrt.be").addPathSegment("search").applySearchQuery(searchQuery).map(HttpUrl.Builder::build)

    private fun <A, B, E> unfoldFlow(initialA: A, next: suspend EitherEffect<E, *>.(A) -> Pair<A, B>?): Flow<Either<E, B>> = flow {
        var initial: A? = initialA
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

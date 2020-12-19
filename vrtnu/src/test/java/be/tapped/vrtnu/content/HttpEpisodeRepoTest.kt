package be.tapped.vrtnu.content

import arrow.core.Either
import be.tapped.vrtnu.ApiResponse
import be.tapped.vrtnu.common.defaultOkHttpClient
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.should
import io.kotest.matchers.types.beOfType
import kotlinx.coroutines.flow.toList

public class HttpEpisodeRepoTest : StringSpec({

    val episodeRepo = HttpEpisodeRepo(defaultOkHttpClient, JsonEpisodeParser())

    "episodes for default search query" {
        val toList = episodeRepo.episodes(ElasticSearchQueryBuilder.SearchQuery()).toList()
        toList
            .forEach {
                it should beOfType<Either.Right<ApiResponse.Success.Content.Episodes>>()
                it.orNull()!!.episodes.shouldNotBeEmpty()
            }
    }
})

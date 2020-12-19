package be.tapped.vrtnu.content

import app.cash.turbine.test
import be.tapped.vrtnu.common.defaultOkHttpClient
import io.kotest.core.spec.style.StringSpec
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
public class HttpEpisodeRepoTest : StringSpec({

    val episodeRepo = HttpEpisodeRepo(defaultOkHttpClient, JsonEpisodeParser())

    "episodes for default search query" {
        episodeRepo.episodes(ElasticSearchQueryBuilder.SearchQuery()).test {
            expectComplete()
        }
    }
})

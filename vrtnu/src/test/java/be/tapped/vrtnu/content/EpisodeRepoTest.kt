package be.tapped.vrtnu.content

import arrow.core.Either
import arrow.fx.coroutines.parTraverse
import be.tapped.vrtnu.ApiResponse
import be.tapped.vrtnu.common.defaultOkHttpClient
import be.tapped.vrtnu.content.ElasticSearchQueryBuilder.SearchQuery
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beOfType
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList

public class EpisodeRepoTest : StringSpec({

    val episodeRepo = HttpEpisodeRepo(defaultOkHttpClient, JsonEpisodeParser())

    "should not make a search call if the requested size is bigger than the max allowed" {
        episodeRepo.episodes(SearchQuery(size = 301)).collect {
            it shouldBe beOfType<Either.Left<ApiResponse.Failure.Content.SearchQuery>>()
        }
    }

    "should not make a search call if the result window is exceeded" {
        episodeRepo.episodes(SearchQuery(size = 10_001)).collect {
            it shouldBe beOfType<Either.Left<ApiResponse.Failure.Content.SearchQuery>>()
        }
    }

    "return episodes by query" {
        val emissions = episodeRepo.episodes(SearchQuery(query = "Terzake")).toList()
        emissions.shouldHaveSize(1)
        emissions.first().orNull()!!.episodes.shouldNotBeEmpty()
    }

    "return episodes by program url" {
        val emissions = episodeRepo.episodes(SearchQuery(programUrl = "//www.vrt.be/vrtnu/a-z/terzake/")).toList()
        emissions.shouldHaveSize(1)
        emissions.first().orNull()!!.episodes.shouldNotBeEmpty()
    }

    "return episode by whatsonId" {
        val whatsonId = "932626788527"
        val emissions = episodeRepo.episodes(SearchQuery(whatsonId = whatsonId)).toList()
        emissions.shouldHaveSize(1)
        emissions.first().orNull()!!.episodes.shouldHaveSize(1)
    }

    "return episodes for all programs" {
        val programRepo = HttpProgramRepo(defaultOkHttpClient, JsonProgramParser())
        programRepo.fetchAZPrograms().map {
            it.programs.parTraverse { program ->
                println("Found ${program.programName}")
                val episodesForProgram = episodeRepo.episodesForProgram(program)
                episodesForProgram.collect { episodes ->
                    episodes shouldBe beOfType<Either.Right<ApiResponse.Success.Content.Episodes>>()
                }
            }
        }
    }
})

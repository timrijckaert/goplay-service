package be.tapped.vier.content

import arrow.fx.coroutines.parTraverse
import be.tapped.vier.CredentialsProvider
import be.tapped.vier.profile.HttpProfileRepo
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldNotBeEmpty

public class VierApiE2ETest : FreeSpec({
    val (username, password) = CredentialsProvider.credentials

    "given the VierAPI" - {
        val vierApi = VierApi()

        "and we are logged in" - {
            val profileRepo = HttpProfileRepo()

            val tokens = profileRepo.fetchTokens(username, password)

            "it should be successful" {
                tokens.shouldBeRight()
            }

            val refreshTokens = tokens.orNull()!!.refreshToken
            "it should be able to refresh the tokens" - {
                val newTokens = profileRepo.refreshTokens(refreshTokens)

                "it should be successful" {
                    newTokens.shouldBeRight()
                }
            }

            val accessToken = tokens.orNull()!!.accessToken
            "it should be able to fetch the user attributes" - {
                val profile = profileRepo.getUserAttributes(accessToken)

                "it should be successful" {
                    profile.shouldBeRight()
                }
            }

            "when fetching the all the programs from A-Z" - {
                val programs = vierApi.fetchPrograms()
                "it should be successful" {
                    programs.shouldBeRight()
                }

                val idToken = tokens.orNull()!!.idToken
                "when fetching episodes" - {
                    programs.orNull()!!
                        .programs
                        .flatMap(Program::playlists)
                        .flatMap(Playlist::episodes)
                        .shuffled()
                        .take(100).parTraverse {
                            val streams = vierApi.streamForEpisodeVideoUuid(idToken, it.id)

                            "it should have found a stream $streams" {
                                streams.shouldBeRight()
                            }
                        }
                }
            }
        }

        "when fetching a single program by url" - {
            val deSlimsteMensTerWereldUrl = "https://www.vier.be/de-slimste-mens-ter-wereld"
            val deSlimsteMensTerWereldUrlSearchKey = SearchHit.Source.SearchKey.Program(deSlimsteMensTerWereldUrl)

            val deSlimsteMensTerWereld = vierApi.fetchProgram(deSlimsteMensTerWereldUrlSearchKey)

            "it should be successful" {
                deSlimsteMensTerWereld.shouldBeRight()
            }
        }

        "searching for an existing program" - {
            val searchResult = vierApi.search("de slimste mens")

            "should be successful" {
                searchResult.shouldBeRight()
            }

            "should have found search hits" {
                searchResult.orNull()!!.hits.shouldNotBeEmpty()
            }

            val searchKeys = searchResult.orNull()!!.hits
                .map { it.source.searchKey }

            searchKeys
                .filterIsInstance<SearchHit.Source.SearchKey.Episode>()
                .parTraverse(vierApi::fetchEpisode)
                .forEach {
                    "api returned an episode: $it" {
                        it.shouldBeRight()
                    }
                }

            searchKeys
                .filterIsInstance<SearchHit.Source.SearchKey.Program>()
                .parTraverse(vierApi::fetchProgram)
                .forEach {
                    "api returned a program: $it" {
                        it.shouldBeRight()
                    }
                }
        }
    }
})

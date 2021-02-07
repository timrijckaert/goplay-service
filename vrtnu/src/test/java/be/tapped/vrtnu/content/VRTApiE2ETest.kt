package be.tapped.vrtnu.content

import arrow.fx.coroutines.parTraverse
import be.tapped.vrtnu.CredentialsProvider
import be.tapped.vrtnu.profile.ProfileRepo
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList

/**
 * E2E test for [VRTApi].
 *
 * We don't have any other test since the backend is out of our hands.
 */
public class VRTApiE2ETest : FreeSpec({

    val (username, password) = CredentialsProvider.credentials

    "A ${VRTApi::class.java.simpleName}" - {
        val vrtApi = VRTApi()
        "fetching categories" - {
            val categories = vrtApi.fetchCategories()
            "it should be successful"  {
                categories.shouldBeRight()
            }

            "the categories should not be empty"  {
                categories.orNull()!!.categories.shouldNotBeEmpty()
            }
        }

        "fetching A-Z programs" - {
            val azPrograms = vrtApi.fetchAZPrograms()

            "it should be successful" {
                azPrograms.shouldBeRight()
            }

            val programs = azPrograms.orNull()!!.programs
            "it should not be empty" {
                programs.shouldNotBeEmpty()

                "fetching the Playlists" - {
                    programs.forEach {
                        "fetching the Playlist of ${it.programName}" - {
                            val playlist = vrtApi.fetchProgramPlaylist(it)

                            "it should be successful" {
                                playlist.shouldBeRight()
                            }
                        }
                    }
                }
            }

            "given a ${ProfileRepo::class.java.simpleName}" - {
                val profileRepo = ProfileRepo()
                "fetching token wrapper" - {
                    val tokens = profileRepo.fetchTokenWrapper(username, password)
                    "it should be successful" {
                        tokens.shouldBeRight()
                    }

                    "refreshing the tokens by refresh token" - {
                        val newTokens = profileRepo.refreshTokenWrapper(tokens.orNull()!!.tokenWrapper.refreshToken)
                        "it should be successful" {
                            newTokens.shouldBeRight()
                        }
                    }

                    "fetching X-VRT-Token" - {
                        val xVRTToken = profileRepo.fetchXVRTToken(username, password)

                        "it should be successful" {
                            xVRTToken.shouldBeRight()
                        }

                        "fetching favorites" - {
                            val favorites = profileRepo.favorites(xVRTToken.orNull()!!.xVRTToken)

                            "it should be successful" {
                                favorites.shouldBeRight()
                            }
                        }

                        "fetching a VRT-Player-Token" - {
                            val vrtPlayerToken = profileRepo.fetchVRTPlayerToken(xVRTToken.orNull()!!.xVRTToken)

                            "it should be successful" {
                                vrtPlayerToken.shouldBeRight()
                            }

                            "fetching a live streams" - {
                                LiveStreams.allLiveStreams.parTraverse {
                                    "fetching live stream $it" - {
                                        val liveStream = vrtApi.getLiveStream(
                                                vrtPlayerToken.orNull()!!.vrtPlayerToken, it.videoId
                                        )
                                        "it should be successful" {
                                            liveStream.shouldBeRight()
                                        }
                                    }
                                }
                            }

                            programs.shuffled().take(50).parTraverse { program ->
                                //TODO use new way to retrieve episodes and seasons
                            }
                        }
                    }
                }
            }
        }

        "fetching a existing program by name" - {
            val program = vrtApi.fetchProgramByName("Het Journaal")

            "it should be successful" {
                program.shouldBeRight()
            }

            "the program should not be null" {
                program.orNull()!!.program shouldNotBe null
            }
        }

        "fetching a non existing program by name" - {
            val nonExistingProgram = vrtApi.fetchProgramByName("VTM Nieuws")

            "it should not be successful" {
                nonExistingProgram.shouldBeRight()
            }

            "the program should be null" {
                nonExistingProgram.orNull()!!.program shouldBe null
            }
        }

        "fetching episodes that exceed the max requested size" - {
            vrtApi.search(ElasticSearchQueryBuilder.SearchQuery(size = 301)).collect {
                "it should not be successful" {
                    it.shouldBeLeft()
                }
            }
        }

        "fetching episodes that exceed the result window" - {
            vrtApi.search(ElasticSearchQueryBuilder.SearchQuery(size = 10_001)).collect {
                "it should not be successful" {
                    it.shouldBeLeft()
                }
            }
        }

        "fetching episodes by query" - {
            val episodes = vrtApi.search(ElasticSearchQueryBuilder.SearchQuery(query = "Terzake")).toList()

            "it should have found one match" {
                episodes.shouldHaveSize(1)
            }

            "it should have found multiple episodes" {
                episodes.first().orNull()!!.searchHits.shouldNotBeEmpty()
            }
        }

        "fetching episodes by whatsonId" - {
            val whatsonId = "963170543527"
            val episodes = vrtApi.search(ElasticSearchQueryBuilder.SearchQuery(whatsonId = whatsonId)).toList()

            "it should have found one match" {
                episodes.shouldHaveSize(1)
            }

            "it should have found 1 episode" {
                episodes.first().orNull()!!.searchHits.shouldHaveSize(1)
            }
        }
    }
})

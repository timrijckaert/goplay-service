package be.tapped.vrtnu.content

import arrow.fx.coroutines.parTraverse
import be.tapped.vrtnu.CredentialsProvider
import be.tapped.vrtnu.profile.ProfileRepo
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList

/**
 * E2E test for [VRTApi].
 *
 * We don't have any other test since the backend is out of our hands.
 */
public class VRTApiE2ETest : BehaviorSpec({

    val (username, password) = CredentialsProvider.credentials

    given("A ${VRTApi::class.java.simpleName}") {
        val vrtApi = VRTApi()
        `when`("fetching categories") {
            val categories = vrtApi.fetchCategories()
            then("it should be successful") {
                categories.shouldBeRight()
            }

            then("the categories should not be empty") {
                categories.orNull()!!.categories.shouldNotBeEmpty()
            }
        }

        `when`("fetching A-Z programs") {
            val azPrograms = vrtApi.fetchAZPrograms()

            then("it should be successful") {
                azPrograms.shouldBeRight()
            }

            then("it should not be empty") {
                azPrograms.orNull()!!.programs.shouldNotBeEmpty()
            }

            and("given a ${ProfileRepo::class.java.simpleName}") {
                val profileRepo = ProfileRepo()
                and("fetching token wrapper") {
                    val tokens = profileRepo.fetchTokenWrapper(username, password)
                    then("it should be successful") {
                        tokens.shouldBeRight()
                    }

                    and("refreshing the tokens by refresh token") {
                        val newTokens = profileRepo.refreshTokenWrapper(tokens.orNull()!!.tokenWrapper.refreshToken)
                        then("it should be successful") {
                            newTokens.shouldBeRight()
                        }
                    }

                    and("fetching X-VRT-Token") {
                        val xVRTToken = profileRepo.fetchXVRTToken(username, password)

                        then("it should be successful") {
                            xVRTToken.shouldBeRight()
                        }

                        and("fetching favorites") {
                            val favorites = profileRepo.favorites(xVRTToken.orNull()!!.xVRTToken)

                            then("it should be successful") {
                                favorites.shouldBeRight()
                            }
                        }

                        and("fetching a VRT-Player-Token") {
                            val vrtPlayerToken = profileRepo.fetchVRTPlayerToken(xVRTToken.orNull()!!.xVRTToken)

                            then("it should be successful") {
                                vrtPlayerToken.shouldBeRight()
                            }

                            and("fetching a live stream") {
                                val liveStream = vrtApi.getStream(vrtPlayerToken.orNull()!!.vrtPlayerToken, VideoId("vualto_een_geo"))
                                then("it should be successful") {
                                    liveStream.shouldBeRight()
                                }
                            }

                            azPrograms.orNull()!!.programs.shuffled().take(50).parTraverse { program ->
                                vrtApi.episodesForProgram(program).collect { episodes ->
                                    then("it should have found episodes for $program successful") {
                                        episodes.shouldBeRight()
                                    }

                                    then("it should have episodes for ${program.programName}") {
                                        episodes.orNull()?.episodes.shouldNotBeNull()
                                    }

                                    episodes.orNull()!!.episodes.shuffled().take(50).parTraverse {
                                        val stream = vrtApi.getStream(vrtPlayerToken.orNull()!!.vrtPlayerToken,
                                            it.videoId,
                                            it.publicationId)
                                        then("it should successful. ${it.id}-${program.programName}: ${it.publicationId}-${it.videoId}") {
                                            stream.shouldBeRight()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        `when`("fetching a existing program by name") {
            val program = vrtApi.fetchProgramByName("Het Journaal")

            then("it should be successful") {
                program.shouldBeRight()
            }

            then("the program should not be null") {
                program.orNull()!!.program shouldNotBe null
            }
        }

        `when`("fetching a non existing program by name") {
            val nonExistingProgram = vrtApi.fetchProgramByName("VTM Nieuws")

            then("it should not be successful") {
                nonExistingProgram.shouldBeRight()
            }

            then("the program should be null") {
                nonExistingProgram.orNull()!!.program shouldBe null
            }
        }

        `when`("fetching episodes that exceed the max requested size") {
            vrtApi.episodes(ElasticSearchQueryBuilder.SearchQuery(size = 301)).collect {
                then("it should not be successful") {
                    it.shouldBeLeft()
                }
            }
        }

        `when`("fetching episodes that exceed the result window") {
            vrtApi.episodes(ElasticSearchQueryBuilder.SearchQuery(size = 10_001)).collect {
                then("it should not be successful") {
                    it.shouldBeLeft()
                }
            }
        }

        `when`("fetching episodes by query") {
            val episodes = vrtApi.episodes(ElasticSearchQueryBuilder.SearchQuery(query = "Terzake")).toList()

            then("it should have found one match") {
                episodes.shouldHaveSize(1)
            }

            then("it should have found multiple episodes") {
                episodes.first().orNull()!!.episodes.shouldNotBeEmpty()
            }
        }

        `when`("fetching episodes by whatsonId") {
            val whatsonId = "932626788527"
            val episodes = vrtApi.episodes(ElasticSearchQueryBuilder.SearchQuery(whatsonId = whatsonId)).toList()


            then("it should have found one match") {
                episodes.shouldHaveSize(1)
            }

            then("it should have found 1 episode") {
                episodes.first().orNull()!!.episodes.shouldHaveSize(1)
            }
        }
    }
})

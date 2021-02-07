package be.tapped.vtmgo.content

import arrow.fx.coroutines.parTraverse
import be.tapped.vtmgo.CredentialsProvider
import be.tapped.vtmgo.profile.HttpAuthenticationRepo
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.types.beOfType
import kotlin.random.Random

public class VTMApiE2ETest : FreeSpec() {

    init {
        val (username, password) = CredentialsProvider.credentials
        "A VTM Api" - {
            val vtmApi = VTMApi()
            "and a ProfileRepo" - {
                val profileRepo = HttpAuthenticationRepo()
                "when we are logged in" - {
                    val token = profileRepo.login(username, password)

                    "it should be successful" {
                        token.shouldBeRight()
                    }

                    "and fetch the profiles" - {
                        "then it should have a non null JWT token" {
                            token.orNull()?.token?.jwt.shouldNotBeNull()
                        }

                        val jwtToken = token.orNull()!!.token.jwt
                        val profiles = profileRepo.getProfiles(jwtToken)

                        "then it should be successful" {
                            profiles.shouldBeRight()
                        }

                        "then it should have at least one profile" {
                            profiles.orNull()!!.profiles.shouldHaveAtLeastSize(1)
                        }

                        profiles.orNull()!!.profiles.parTraverse { profile ->
                            "fetching favorites for $profile" - {
                                val favorites = vtmApi.fetchMyFavorites(jwtToken, profile)

                                "it should be successful" {
                                    favorites.shouldBeRight()
                                }
                            }
                        }

                        val profile = profiles.orNull()!!.profiles.first()

                        "and doing a search" - {
                            val searchResults = vtmApi.search(jwtToken, profile, "Code van Coppens")

                            "it should be successful" {
                                searchResults.shouldBeRight()
                            }
                        }

                        "and fetching categories" - {
                            val categories = vtmApi.fetchCategories(jwtToken, profile)

                            "it should be successful" {
                                categories.shouldBeRight()
                            }
                        }

                        "and fetching the live channels" - {
                            val liveChannels = vtmApi.fetchChannels(jwtToken, profile)

                            "it should be successful" {
                                liveChannels.shouldBeRight()
                            }

                            liveChannels.orNull()!!.channels.parTraverse {
                                "fetching the stream for $it" - {
                                    val liveStream = vtmApi.fetchStream(it)

                                    "should be successful" {
                                        liveStream.shouldBeRight()
                                    }

                                    "should be of correct type" {
                                        liveStream.orNull()!!.stream should beOfType<AnvatoStream.Live>()
                                    }
                                }
                            }
                        }

                        StoreFrontType.values().toList().parTraverse {
                            "and fetching the $it storefront" - {
                                val storeFronts = vtmApi.fetchStoreFront(jwtToken, profile, it)

                                "it should be successful" {
                                    storeFronts.shouldBeRight()
                                }

                                storeFronts.orNull()!!.rows.flatMap { storeFront ->
                                    when (storeFront) {
                                        is StoreFront.CarouselStoreFront -> storeFront.teasers.map(CarouselTeaser::target)
                                        is StoreFront.DefaultSwimlaneStoreFront -> storeFront.teasers.map(DefaultSwimlaneTeaser::target)
                                        is StoreFront.ContinueWatchingStoreFront -> storeFront.teasers.map(ContinueWatchingTeaser::target)
                                        is StoreFront.MyListStoreFront -> storeFront.teasers.map(MyListTeaser::target)
                                        is StoreFront.MarketingStoreFront -> listOf(storeFront.teaser.target)
                                        is StoreFront.ProfileSwitcherStoreFront -> emptyList()
                                    }
                                }.filter { resp ->
                                    val target = resp.asTarget
                                    target is TargetResponse.Target.Movie || target is TargetResponse.Target.Episode
                                }.shuffled().take(100).parTraverse { target ->
                                    "${Random.nextInt()} when fetching video streams for $target" - {
                                        val streams = when (val t = target.asTarget) {
                                            is TargetResponse.Target.Movie -> vtmApi.fetchStream(t)
                                            is TargetResponse.Target.Episode -> vtmApi.fetchStream(t)
                                            else -> throw IllegalStateException("Can never happen but compiler is not smart enough")
                                        }

                                        "it should be successful" {
                                            streams.shouldBeRight()
                                        }
                                    }
                                }
                            }
                        }

                        "and fetch program from A-Z" - {
                            val azPrograms = vtmApi.fetchAZ(jwtToken, profile)

                            "then it should be successful" {
                                azPrograms.shouldBeRight()
                            }

                            "then the list should not be empty" {
                                azPrograms.orNull()!!.catalog.shouldNotBeEmpty()
                            }

                            azPrograms.orNull()!!.catalog.map { it.target.asTarget }.filterIsInstance<TargetResponse.Target.Program>().shuffled()
                                    .take(100).forEach { program ->
                                        "fetching program details with id ${program.id}" - {
                                            val programDetails = vtmApi.fetchProgram(program, jwtToken, profile)

                                            "then it should be successful" {
                                                programDetails.shouldBeRight()
                                            }
                                        }
                                    }
                        }
                    }
                }
            }
        }
    }
}

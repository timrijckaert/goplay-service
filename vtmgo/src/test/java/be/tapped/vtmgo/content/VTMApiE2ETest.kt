package be.tapped.vtmgo.content

import be.tapped.vtmgo.CredentialsProvider
import be.tapped.vtmgo.profile.HttpProfileRepo
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.types.beOfType

public class VTMApiE2ETest : FreeSpec() {

    init {
        val (username, password) = CredentialsProvider.credentials
        "A VTM Api" - {
            val vtmApi = VTMApi()
            "and a ProfileRepo" - {
                val profileRepo = HttpProfileRepo()
                "when we are logged in" - {
                    val token = profileRepo.login(username, password)

                    "and fetch the profiles" - {
                        "then it should have a non null JWT token" {
                            token.orNull()?.jwt.shouldNotBeNull()
                        }

                        val jwtToken = token.orNull()!!.jwt
                        val profiles = profileRepo.getProfiles(jwtToken)

                        "then it should be successful" {
                            profiles.shouldBeRight()
                        }

                        "then it should have at least one profile" {
                            profiles.orNull()!!.profiles.shouldHaveAtLeastSize(1)
                        }

                        profiles.orNull()!!.profiles.forEach { profile ->
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

                            liveChannels.orNull()!!.channels.forEach {
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

                        StoreFrontType.values().forEach {
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
                                }
                                    .shuffled()
                                    .take(50)
                                    .forEachIndexed { index, target ->
                                        "when fetching video streams for $index: $target" - {
                                            val t = target.asTarget
                                            val streams = vtmApi.fetchStream(t)
                                            when (t) {
                                                is TargetResponse.Target.Movie,
                                                is TargetResponse.Target.Episode,
                                                -> {
                                                    "it should be successful" {
                                                        streams.shouldBeRight()
                                                    }
                                                }
                                                is TargetResponse.Target.Program,
                                                is TargetResponse.Target.External,
                                                -> {
                                                    "it should not be successful" {
                                                        streams.shouldBeLeft()
                                                    }
                                                }
                                            }
                                        }
                                    }
                            }
                        }

                        "and fetch program from A-2" - {
                            val azPrograms = vtmApi.fetchAZ(jwtToken, profile)

                            "then it should be successful" {
                                azPrograms.shouldBeRight()
                            }

                            "then the list should not be empty" {
                                azPrograms.orNull()!!.catalog.shouldNotBeEmpty()
                            }

                            azPrograms.orNull()!!.catalog
                                .map { it.target.asTarget }
                                .filterIsInstance<TargetResponse.Target.Program>()
                                .shuffled()
                                .take(100)
                                .forEach { program ->
                                    "fetching program details with id ${program.id}" - {
                                        val programDetails = vtmApi.fetchProgram(program, jwtToken, profile)

                                        "then it should be successful" {
                                            programDetails.shouldBeRight()
                                        }
                                    }
                                }
                        }
                    }

                    "it should be successful" {
                        token.shouldBeRight()
                    }
                }
            }
        }
    }
}

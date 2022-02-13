package be.tapped.goplay.e2e

import arrow.fx.coroutines.parTraverse
import be.tapped.goplay.Credentials
import be.tapped.goplay.GoPlayApi
import be.tapped.goplay.content.Program
import be.tapped.goplay.epg.GoPlayBrand
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FreeSpec

/**
 * This test hits the real https://goplay.be/ site.
 * It is here to detect any regression on the implementation since we don't own/control the backend.
 */
internal class GoPlayE2ETest : FreeSpec({

    "retrieving all programs" - {
        val allPrograms = GoPlayApi.fetchPrograms()

        "should be successful" {
            allPrograms.shouldBeRight()
        }

        "and retrieving the details by link" - {
            allPrograms.shouldBeRight().programs.parTraverse {
                val detail = GoPlayApi.fetchProgramByLink(it.link)

                "for ${it.title}" - {
                    "should be successful" {
                        detail.shouldBeRight()
                    }
                }
            }
        }

        "and retrieving the details by id" - {
            val (username, password) = Credentials.default
            val tokenWrapper = GoPlayApi.fetchTokens(username, password).shouldBeRight().token

            allPrograms.shouldBeRight().programs.parTraverse {
                val detail = GoPlayApi.fetchProgramById(it.id)

                "for ${it.title}" - {
                    "should be successful" {
                        detail.shouldBeRight()
                    }

                    "retrieving the episodes" - {
                        val episodes = detail.shouldBeRight().program.playlists.flatMap(Program.Detail.Playlist::episodes)

                        episodes.parTraverse { episode ->
                            "retrieving the stream for ${episode.title}" - {
                                val stream = GoPlayApi.streamByVideoUuid(episode.videoUuid, tokenWrapper.idToken)

                                "should be successful" {
                                    stream.shouldBeRight()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    "retrieves the most popular programs" {
        GoPlayApi.fetchPopularPrograms().shouldBeRight()
    }

    GoPlayBrand.values().forEach { brand ->
        "when retrieving the most popular programs for $brand" - {
            val mostPopularPrograms = GoPlayApi.fetchPopularPrograms(brand)

            "it should be successful" {
                mostPopularPrograms.shouldBeRight()
            }
        }
    }

    "retrieving all categories" - {
        val categories = GoPlayApi.fetchCategories().shouldBeRight().categories

        categories.parTraverse {
            "retrieving programs by category ${it.name}" - {
                val programsByCategory = GoPlayApi.fetchProgramsByCategory(it.id)

                "should be successful" {
                    programsByCategory.shouldBeRight()
                }
            }
        }
    }
})

package be.tapped.goplay.e2e

import arrow.fx.coroutines.parTraverse
import be.tapped.goplay.content.AllProgramsHtmlJsonExtractor
import be.tapped.goplay.content.HttpProgramRepo
import be.tapped.goplay.content.ProgramDetailHtmlJsonExtractor
import be.tapped.goplay.httpClient
import be.tapped.goplay.jsonSerializer
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FreeSpec

internal class ProgramRepoTest : FreeSpec({
    val sut = HttpProgramRepo(httpClient, jsonSerializer, AllProgramsHtmlJsonExtractor(), ProgramDetailHtmlJsonExtractor())

    "when retrieving all programs" - {
        val allPrograms = sut.fetchPrograms()

        "it should be successful" {
            allPrograms.shouldBeRight()
        }

        "and retrieving the details by link" - {
            allPrograms.shouldBeRight().programs.parTraverse {
                val detail = sut.fetchProgramByLink(it.link)

                "for ${it.title}" - {
                    "it should be successful" {
                        detail.shouldBeRight()
                    }
                }
            }
        }

        "and retrieving the details by id" - {
            allPrograms.shouldBeRight().programs.parTraverse {
                val detail = sut.fetchProgramById(it.id)

                "for ${it.title}" - {
                    "it should be successful" {
                        detail.shouldBeRight()
                    }
                }
            }
        }
    }
})

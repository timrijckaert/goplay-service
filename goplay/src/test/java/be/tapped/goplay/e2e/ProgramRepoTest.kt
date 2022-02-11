package be.tapped.goplay.e2e

import be.tapped.goplay.content.HtmlJsonProgramExtractor
import be.tapped.goplay.content.httpProgramRepo
import be.tapped.goplay.httpClient
import be.tapped.goplay.jsonSerializer
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.ShouldSpec

internal class ProgramRepoTest : ShouldSpec({
    val sut = httpProgramRepo(httpClient, HtmlJsonProgramExtractor(jsonSerializer))

    should("retrieve all programs") {
        sut.fetchPrograms().shouldBeRight()
    }
})

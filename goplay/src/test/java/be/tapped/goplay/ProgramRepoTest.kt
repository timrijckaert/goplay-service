package be.tapped.goplay

import be.tapped.goplay.content.HtmlProgramParser
import be.tapped.goplay.content.httpClient
import be.tapped.goplay.content.httpProgramRepo
import be.tapped.goplay.content.jsonSerializer
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.ShouldSpec

internal class ProgramRepoTest : ShouldSpec({
    val sut = httpProgramRepo(httpClient, HtmlProgramParser(jsonSerializer))

    should("retrieve all programs") {
        sut.fetchPrograms().shouldBeRight()
    }
})

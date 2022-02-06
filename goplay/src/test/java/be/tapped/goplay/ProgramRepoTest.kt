package be.tapped.goplay

import be.tapped.goplay.content.HttpProgramRepo
import be.tapped.goplay.content.SearchHit
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.ShouldSpec

internal class ProgramRepoTest : ShouldSpec({
    val sut = HttpProgramRepo()

    should("retrieve all programs") {
        sut.fetchPrograms().shouldBeRight()
    }

    xshould("fetch a program by url") {
        sut.fetchProgram(SearchHit.Source.SearchKey.Program(""))
    }
})

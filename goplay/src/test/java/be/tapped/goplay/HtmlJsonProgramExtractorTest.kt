package be.tapped.goplay

import be.tapped.goplay.content.HtmlJsonProgramExtractor
import be.tapped.goplay.content.jsonSerializer
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldHaveSize

internal class HtmlJsonProgramExtractorTest : ShouldSpec({
    should("extract the programs from the HTML page") {
        val programsHtml = readFromResources("programmas.html")
        val sut = HtmlJsonProgramExtractor(jsonSerializer)
        val programs = sut.parse(programsHtml)
        programs.shouldBeRight().shouldHaveSize(278)
    }
})

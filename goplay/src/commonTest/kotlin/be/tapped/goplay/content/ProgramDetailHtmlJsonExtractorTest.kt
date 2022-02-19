package be.tapped.goplay.content

import be.tapped.goplay.readFromResources
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.ShouldSpec

internal class ProgramDetailHtmlJsonExtractorTest : ShouldSpec({
    should("extract the program JSON information from the HTML page") {
        val programDetailHtml = readFromResources("de-slimste-mens-ter-wereld.html")
        val sut = ProgramDetailHtmlJsonExtractor()
        val programDetailJson = sut.parse(programDetailHtml)
        programDetailJson.shouldBeRight()
    }
})

package be.tapped.goplay.content

import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize

public class HtmlProgramParserTest : StringSpec({

    "should be able to parse all programs" {
        val allProgramsHTML = javaClass.classLoader?.getResourceAsStream("programs.html")!!.reader().readText()
        val allPrograms = HtmlProgramParser(JsoupParser()).parse(allProgramsHTML)
        allPrograms.shouldBeRight()
        allPrograms.orNull()!!.shouldHaveSize(113)
    }
})

package be.tapped.vrtnu.content

import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize

public class JsonProgramParserTest : StringSpec({

    "should be able to parse the programs" {
        val programJson = javaClass.classLoader?.getResourceAsStream("programs.json")!!.reader().readText()
        val programs = JsonProgramParser(ProgramSanitizer(UrlPrefixMapper())).parse(programJson).shouldBeRight()
        programs shouldHaveSize 495
    }
})

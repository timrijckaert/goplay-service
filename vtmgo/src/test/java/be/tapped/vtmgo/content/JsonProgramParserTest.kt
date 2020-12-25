package be.tapped.vtmgo.content

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize

public class JsonProgramParserTest : StringSpec() {
    init {
        "should be able to parse" {
            val programDetailsJson = javaClass.classLoader?.getResourceAsStream("program-details.json")!!.reader().readText()
            val programs = JsonProgramParser().parse(programDetailsJson).orNull()!!
            programs.seasons shouldHaveSize 1
            programs.seasons.first().episodes shouldHaveSize 17
        }

        "should be able to parse nullable things" {
            val programDetailsJson = javaClass.classLoader?.getResourceAsStream("program-details-2.json")!!.reader().readText()
            val programs = JsonProgramParser().parse(programDetailsJson).orNull()!!
            programs.seasons shouldHaveSize 1
            programs.seasons.first().episodes shouldHaveSize 1
        }
    }
}

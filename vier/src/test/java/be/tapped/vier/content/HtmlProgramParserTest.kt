package be.tapped.vier.content

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

public class HtmlProgramParserTest : StringSpec({

    "should be able to parse" {
        val dancingWithTheStars = javaClass.classLoader?.getResourceAsStream("dancing-with-the-stars.html")!!.reader().readText()
        val dancingWithTheStarsProgram = HtmlFullProgramParser(JsoupParser()).parse(dancingWithTheStars).orNull()!!
        dancingWithTheStarsProgram.id shouldBe "7460292d-5e0c-4792-a6a4-cc2825b124c4"
        dancingWithTheStarsProgram.playlists shouldHaveSize 5
    }
})

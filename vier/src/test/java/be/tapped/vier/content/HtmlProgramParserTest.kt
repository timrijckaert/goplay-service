package be.tapped.vier.content

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

public class HtmlProgramParserTest : StringSpec({

    "should be able to parse `Dancing with the Stars`" {
        val dancingWithTheStars = javaClass.classLoader?.getResourceAsStream("dancing-with-the-stars.html")!!.reader().readText()
        val dancingWithTheStarsProgram = HtmlFullProgramParser(JsoupParser()).parse(dancingWithTheStars)
        dancingWithTheStarsProgram.orNull()!!.id shouldBe "7460292d-5e0c-4792-a6a4-cc2825b124c4"
        dancingWithTheStarsProgram.orNull()!!.playlists shouldHaveSize 5
        dancingWithTheStarsProgram.orNull()!!.playlists.flatMap(Program.Playlist::episodes) shouldHaveSize 157
    }

    "should be able to parse `Over de Oceaan`" {
        val overDeOceaan = javaClass.classLoader?.getResourceAsStream("over-de-oceaan.html")!!.reader().readText()
        val overDeOceaanProgram = HtmlFullProgramParser(JsoupParser()).parse(overDeOceaan)
        overDeOceaanProgram.orNull()!!.id shouldBe "06e209f9-092e-421e-9499-58c62c292b98"
        overDeOceaanProgram.orNull()!!.playlists shouldHaveSize 1
        overDeOceaanProgram.orNull()!!.playlists.flatMap(Program.Playlist::episodes) shouldHaveSize 1
    }
})

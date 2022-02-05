package be.tapped.vtmgo.content

import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize

public class JsonPagedTeaserContentParserTest : StringSpec({
    "should be able to parse" {
        val azJson = javaClass.classLoader?.getResourceAsStream("az-programs.json")!!.reader().readText()
        val azCatalog = JsonPagedTeaserContentParser().parse(azJson).shouldBeRight()
        azCatalog shouldHaveSize 564
    }
})

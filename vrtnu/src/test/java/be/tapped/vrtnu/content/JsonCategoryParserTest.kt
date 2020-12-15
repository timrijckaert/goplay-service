package be.tapped.vrtnu.content

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

public class JsonCategoryParserTest : StringSpec() {

    init {
        "should be able to parse" {
            val categoriesJson = javaClass.classLoader?.getResourceAsStream("categories.json")!!.reader().readText()
            val categories = JsonCategoryParser().parse(categoriesJson).orNull()!!
            categories shouldHaveSize 19
            categories.map(Category::name) shouldBe listOf(
                "met-audiodescriptie",
                "cultuur",
                "docu",
                "entertainment",
                "films",
                "human-interest",
                "humor",
                "voor-kinderen",
                "koken",
                "levensbeschouwing",
                "lifestyle",
                "muziek",
                "nieuws-en-actua",
                "nostalgie",
                "series",
                "sport",
                "talkshows",
                "met-gebarentaal",
                "wetenschap-en-natuur",
            )
        }
    }
}

package be.tapped.goplay.content

import be.tapped.goplay.content.Category.Id
import be.tapped.goplay.jsonSerializer
import be.tapped.goplay.readFromResources
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.serialization.decodeFromString

internal class ContentTreeJsonParserTest : ShouldSpec({
    should("parse the content tree") {
        val contentTreeJson = readFromResources("content_tree.json")
        val sut = contentTreeJsonParser()
        val contentRoot = sut.parseJsonToContentRoot(jsonSerializer.decodeFromString(contentTreeJson))
        contentRoot.shouldBeRight().categories.shouldContainExactly(
            Category(Id("5287"), "Humor"),
            Category(Id("5284"), "Entertainment"),
            Category(Id("5283"), "Docu"),
            Category(Id("5285"), "Fictie"),
            Category(Id("5282"), "Actua & Sport"),
            Category(Id("5290"), "Spelprogramma"),
            Category(Id("5286"), "Film"),
            Category(Id("5288"), "Lifestyle"),
            Category(Id("5289"), "Reality")
        )
    }
})

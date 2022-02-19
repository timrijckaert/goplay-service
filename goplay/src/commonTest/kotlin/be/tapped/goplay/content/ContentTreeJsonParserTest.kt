package be.tapped.goplay.content

import be.tapped.goplay.content.Category.Id
import be.tapped.goplay.jsonSerializer
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.serialization.decodeFromString
import org.intellij.lang.annotations.Language

internal class ContentTreeJsonParserTest : ShouldSpec({
    should("parse the content tree") {
        val contentRoot = contentTreeJsonParser().parseJsonToContentRoot(jsonSerializer.decodeFromString(CONTENT_TREE))
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
        contentRoot.shouldBeRight().programs.shouldContainExactly(ContentRoot.Program(Program.Id("0f9c8a76-7934-4ebe-b927-fd551deb16d3"), Category.Id("5283"), 226))
    }
})

@Language("JSON")
private const val CONTENT_TREE: String =
    """
        {
  "videos": {
    "454a8274-d476-41a8-a6d4-5ab68e6694b8": {
      "playlist": "16e13282-f616-478c-9ebc-046c7c89bac9",
      "position": 0,
      "published": true,
      "duration": 2800,
      "publishedOn": 1644353280,
      "pageUuid": "69e3386b-d793-4edb-89b5-b0df0c9206af"
    }
  },
  "playlists": {
    "16e13282-f616-478c-9ebc-046c7c89bac9": {
      "season": 1,
      "program": "3a2aa579-5116-4f23-b97d-6d7b1cf0bbb4"
    }
  },
  "categories": {
    "5287": "Humor",
    "5284": "Entertainment",
    "5283": "Docu",
    "5285": "Fictie",
    "5282": "Actua \u0026 Sport",
    "5290": "Spelprogramma",
    "5286": "Film",
    "5288": "Lifestyle",
    "5289": "Reality"
  },
  "programs": {
    "0f9c8a76-7934-4ebe-b927-fd551deb16d3": {
      "category": 5283,
      "popularity": 226
    }
  }
}
    """

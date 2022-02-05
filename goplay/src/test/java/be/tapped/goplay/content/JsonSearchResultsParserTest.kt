package be.tapped.goplay.content

import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize

public class JsonSearchResultsParserTest : StringSpec({

    "should be able to parse" {
        val searchResultJson = javaClass.classLoader?.getResourceAsStream("search-result.json")!!.reader().readText()
        val searchResult = JsonSearchResultsParser().parse(searchResultJson).shouldBeRight()
        searchResult shouldHaveSize 20
    }
})

package be.tapped.goplay.e2e

import be.tapped.goplay.content.httpSearchRepo
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldNotBeEmpty

internal class SearchRepoTest : ShouldSpec({
    should("find results") {
        val sut = httpSearchRepo()
        val search = sut.search("de slimste mens").shouldBeRight()
        search.hits.shouldNotBeEmpty()
    }
})

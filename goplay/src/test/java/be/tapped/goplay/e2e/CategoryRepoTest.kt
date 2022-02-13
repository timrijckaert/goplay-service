package be.tapped.goplay.e2e

import be.tapped.goplay.content.categoryRepo
import be.tapped.goplay.content.contentRootRepo
import be.tapped.goplay.content.contentTreeJsonParser
import be.tapped.goplay.httpClient
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.ShouldSpec

internal class CategoryRepoTest : ShouldSpec({
    should("be able to fetch categories") {
        val categories = categoryRepo(contentRootRepo(httpClient, contentTreeJsonParser())).fetchCategories()
        categories.shouldBeRight()
    }
})

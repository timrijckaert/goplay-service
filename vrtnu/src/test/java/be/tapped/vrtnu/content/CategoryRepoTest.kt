package be.tapped.vrtnu.content

import be.tapped.vrtnu.common.defaultOkHttpClient
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldNotBeEmpty

public class CategoryRepoTest : StringSpec({
    val categoryRepo = HttpCategoryRepo(defaultOkHttpClient, JsonCategoryParser())

    "fetch categories" {
        val categories = categoryRepo.fetchCategories()
        categories.shouldBeRight()
        categories.orNull()!!.categories.shouldNotBeEmpty()
    }
})

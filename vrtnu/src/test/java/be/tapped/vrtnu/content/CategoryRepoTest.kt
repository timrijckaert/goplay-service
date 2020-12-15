package be.tapped.vrtnu.content

import arrow.core.Either
import be.tapped.vrtnu.ApiResponse
import be.tapped.vrtnu.common.defaultOkHttpClient
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.should
import io.kotest.matchers.types.beOfType

public class CategoryRepoTest : StringSpec({
    val categoryRepo = HttpCategoryRepo(defaultOkHttpClient, JsonCategoryParser())

    "fetch categories" {
        val categories = categoryRepo.fetchCategories()
        categories should beOfType<Either.Right<ApiResponse.Success.Content.Categories>>()
        categories.orNull()!!.categories.shouldNotBeEmpty()
    }
})

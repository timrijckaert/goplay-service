package be.tapped.goplay.content

import arrow.core.Either
import be.tapped.goplay.ApiResponse

internal fun interface CategoryRepo {
    suspend fun fetchCategories(): Either<ApiResponse.Failure, ApiResponse.Success.Content.Categories>
}

internal fun categoryRepo(contentTreeRepo: ContentTreeRepo): CategoryRepo =
    CategoryRepo {
        contentTreeRepo.fetchContentTree().map(ContentRoot::categories).mapLeft { ApiResponse.Failure.Content.NoCategories }.map(ApiResponse.Success.Content::Categories)
    }

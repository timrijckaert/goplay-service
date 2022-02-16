package be.tapped.goplay.content

import arrow.core.Either
import be.tapped.goplay.Categories
import be.tapped.goplay.Failure

internal fun interface CategoryRepo {
    suspend fun fetchCategories(): Either<Failure, Categories>
}

internal fun categoryRepo(contentTreeRepo: ContentTreeRepo): CategoryRepo =
    CategoryRepo {
        contentTreeRepo.fetchContentTree().map(ContentRoot::categories).mapLeft { Failure.Content.NoCategories }.map(::Categories)
    }

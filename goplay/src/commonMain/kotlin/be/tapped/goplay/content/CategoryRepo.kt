package be.tapped.goplay.content

import arrow.core.Either
import be.tapped.goplay.Categories
import be.tapped.goplay.CoroutineDispatchers
import be.tapped.goplay.Failure
import be.tapped.goplay.dispatchers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal fun interface CategoryRepo {
    suspend fun fetchCategories(): Either<Failure, Categories>
}

internal fun categoryRepo(contentTreeRepo: ContentTreeRepo, dispatchers: CoroutineDispatchers): CategoryRepo =
    CategoryRepo {
        withContext(dispatchers.io) {
            contentTreeRepo.fetchContentTree().map(ContentRoot::categories).mapLeft { Failure.Content.NoCategories }.map(::Categories)
        }
    }

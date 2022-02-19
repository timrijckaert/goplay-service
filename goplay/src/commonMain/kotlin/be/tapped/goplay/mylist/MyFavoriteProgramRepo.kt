package be.tapped.goplay.mylist

import arrow.core.Either
import arrow.core.computations.either
import be.tapped.goplay.Failure
import be.tapped.goplay.apiGoPlay
import be.tapped.goplay.content.Program
import be.tapped.goplay.profile.IdToken
import be.tapped.goplay.safeGet
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal fun interface MyFavoriteProgramRepo {
    suspend fun fetchMyFavoritePrograms(idToken: IdToken): Either<Failure, List<Program.Id>>
}

/**
 *
 * Example
 *
 * ```json
 * [
 *   {
 *     "createdAt": 1644778145555,
 *     "programId": "4f6a6d0c-4afa-4547-a4ea-f0ad723615d8"
 *   }
 * ]
 * ```
 */
internal fun myFavoriteProgramRepo(client: HttpClient): MyFavoriteProgramRepo =
    MyFavoriteProgramRepo {
        either {
            withContext(Dispatchers.IO) {
                client.safeGet<List<FavoriteItem>>("$apiGoPlay/my-list") {
                    defaultAuthorizationHeader(it)
                }.bind().map { Program.Id(it.programId) }
            }
        }
    }

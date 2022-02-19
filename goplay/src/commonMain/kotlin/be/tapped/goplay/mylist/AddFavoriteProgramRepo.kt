package be.tapped.goplay.mylist

import arrow.core.Either
import arrow.core.Either.Companion.catch
import be.tapped.goplay.CoroutineDispatchers
import be.tapped.goplay.apiGoPlay
import be.tapped.goplay.content.Program
import be.tapped.goplay.profile.IdToken
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.withContext

internal fun interface AddFavoriteProgramRepo {
    suspend fun addFavoriteProgram(programId: Program.Id, idToken: IdToken): Either<Throwable, Unit>
}

internal fun addFavoriteProgramRepo(client: HttpClient, dispatchers: CoroutineDispatchers): AddFavoriteProgramRepo =
    AddFavoriteProgramRepo { programId, idToken ->
        catch {
            withContext(dispatchers.io) {
                client.post("$apiGoPlay/my-list") {
                    contentType(ContentType.Application.Json)
                    defaultAuthorizationHeader(idToken)
                    body = FavoriteItem(programId.id)
                }
            }
        }
    }

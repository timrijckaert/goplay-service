package be.tapped.goplay.mylist

import arrow.core.Either
import arrow.core.Either.Companion.catch
import be.tapped.goplay.apiGoPlay
import be.tapped.goplay.content.Program
import be.tapped.goplay.profile.IdToken
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.parameter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal fun interface RemoveFavoriteProgramRepo {
    suspend fun removeFavoriteProgram(programId: Program.Id, idToken: IdToken): Either<Throwable, Unit>
}

internal fun removeFavoriteRepo(client: HttpClient): RemoveFavoriteProgramRepo =
    RemoveFavoriteProgramRepo { programId, idToken ->
        catch {
            withContext(Dispatchers.IO) {
                client.delete<Unit>("$apiGoPlay/my-list-item") {
                    defaultAuthorizationHeader(idToken)
                    parameter("programId", programId.id)
                }
            }
        }
    }

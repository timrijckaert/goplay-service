package be.tapped.goplay.mylist

import be.tapped.goplay.content.Program

internal fun interface RemoveFavoriteProgramRepo {
    suspend fun removeFavoriteProgram(programId: Program.Id)
}

internal fun removeFavoriteRepo(): RemoveFavoriteProgramRepo =
    RemoveFavoriteProgramRepo {
        TODO()
    }

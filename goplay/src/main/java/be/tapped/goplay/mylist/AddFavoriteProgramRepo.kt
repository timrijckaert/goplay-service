package be.tapped.goplay.mylist

import be.tapped.goplay.content.Program

internal fun interface AddFavoriteProgramRepo {
    suspend fun addFavoriteProgram(programId: Program.Id)
}

internal fun addFavoriteProgramRepo(): AddFavoriteProgramRepo =
    AddFavoriteProgramRepo {
        TODO()
    }

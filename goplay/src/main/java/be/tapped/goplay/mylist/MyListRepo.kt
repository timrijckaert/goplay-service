package be.tapped.goplay.mylist

import arrow.core.Either
import arrow.core.right
import be.tapped.goplay.ApiResponse
import be.tapped.goplay.content.Program

internal interface MyListRepo : MyFavoriteProgramRepo, AddFavoriteProgramRepo, RemoveFavoriteRepo

internal class HttpMyListRepo(
    myFavoriteProgramRepo: MyFavoriteProgramRepo,
    addFavoriteProgramRepo: AddFavoriteProgramRepo,
    removeFavoriteRepo: RemoveFavoriteRepo,
) :
    MyListRepo,
    MyFavoriteProgramRepo by myFavoriteProgramRepo,
    AddFavoriteProgramRepo by addFavoriteProgramRepo,
    RemoveFavoriteRepo by removeFavoriteRepo

internal fun interface MyFavoriteProgramRepo {
    suspend fun fetchMyFavoritePrograms(): Either<ApiResponse.Failure, List<Program.Id>>
}

internal fun myFavoriteProgramRepo(): MyFavoriteProgramRepo =
    MyFavoriteProgramRepo(emptyList<Program.Id>()::right)

internal fun interface AddFavoriteProgramRepo {
    suspend fun addFavoriteProgram(programId: Program.Id)
}

internal fun addFavoriteProgramRepo(): AddFavoriteProgramRepo =
    AddFavoriteProgramRepo {
        TODO()
    }

internal fun interface RemoveFavoriteRepo {
    suspend fun removeFavoriteProgram(programId: Program.Id)
}

internal fun removeFavoriteRepo(): RemoveFavoriteRepo =
    RemoveFavoriteRepo {
        TODO()
    }

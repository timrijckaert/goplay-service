package be.tapped.goplay.mylist

internal interface MyListRepo : MyFavoriteProgramRepo, AddFavoriteProgramRepo, RemoveFavoriteProgramRepo

internal class HttpMyListRepo(
    myFavoriteProgramRepo: MyFavoriteProgramRepo,
    addFavoriteProgramRepo: AddFavoriteProgramRepo,
    removeFavoriteProgramRepo: RemoveFavoriteProgramRepo,
) :
    MyListRepo,
    MyFavoriteProgramRepo by myFavoriteProgramRepo,
    AddFavoriteProgramRepo by addFavoriteProgramRepo,
    RemoveFavoriteProgramRepo by removeFavoriteProgramRepo

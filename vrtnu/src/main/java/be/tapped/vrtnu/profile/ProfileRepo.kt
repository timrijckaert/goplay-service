package be.tapped.vrtnu.profile

import be.tapped.common.DefaultCookieJar
import be.tapped.common.ReadOnlyCookieJar
import be.tapped.vrtnu.common.defaultOkHttpClient
import okhttp3.OkHttpClient

class ProfileRepo(
    cookieJar: ReadOnlyCookieJar = DefaultCookieJar(),
    client: OkHttpClient = defaultOkHttpClient.newBuilder().cookieJar(cookieJar).build(),
    tokenRepo: TokenRepo = HttpTokenRepo(client, cookieJar),
    favoritesRepo: FavoritesRepo = HttpFavoritesRepo(client, JsonFavoriteParser()),
) : TokenRepo by tokenRepo,
    FavoritesRepo by favoritesRepo

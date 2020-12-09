package be.tapped.vrtnu.profile

import be.tapped.common.internal.DefaultCookieJar
import be.tapped.common.internal.ReadOnlyCookieJar
import be.tapped.vrtnu.common.defaultOkHttpClient
import okhttp3.OkHttpClient

public class ProfileRepo(
    cookieJar: ReadOnlyCookieJar = DefaultCookieJar(),
    client: OkHttpClient = defaultOkHttpClient.newBuilder().cookieJar(cookieJar).build(),
    tokenRepo: TokenRepo = HttpTokenRepo(client, cookieJar),
    favoritesRepo: FavoritesRepo = HttpFavoritesRepo(client, JsonFavoriteParser()),
) : TokenRepo by tokenRepo,
    FavoritesRepo by favoritesRepo

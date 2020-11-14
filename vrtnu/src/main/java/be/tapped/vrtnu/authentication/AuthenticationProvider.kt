package be.tapped.vrtnu.authentication

import be.tapped.vrtnu.common.defaultOkHttpClient
import be.tapped.vtmgo.common.DefaultCookieJar
import be.tapped.vtmgo.common.ReadOnlyCookieJar
import okhttp3.OkHttpClient

class AuthenticationProvider(
    cookieJar: ReadOnlyCookieJar = DefaultCookieJar(),
    client: OkHttpClient = defaultOkHttpClient.newBuilder().cookieJar(cookieJar).build(),
    tokenRepo: TokenRepo = HttpTokenRepo(client, cookieJar),
) : TokenRepo by tokenRepo

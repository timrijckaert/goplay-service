package be.tapped.vrtnu.authentication

import be.tapped.vtmgo.common.DefaultCookieJar
import be.tapped.vtmgo.common.ReadOnlyCookieJar
import com.moczul.ok2curl.CurlInterceptor
import okhttp3.OkHttpClient

class AuthenticationProvider(
    cookieJar: ReadOnlyCookieJar = DefaultCookieJar,
    client: OkHttpClient = OkHttpClient.Builder()
        .addNetworkInterceptor(CurlInterceptor { message -> println("$message\n\r") })
        .cookieJar(cookieJar)
        .build(),
    tokenProvider: TokenProvider = HttpTokenProvider(client, cookieJar),
) : TokenProvider by tokenProvider

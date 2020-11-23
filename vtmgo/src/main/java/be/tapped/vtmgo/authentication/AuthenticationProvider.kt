package be.tapped.vtmgo.authentication

import be.tapped.common.DefaultCookieJar
import be.tapped.common.ReadOnlyCookieJar
import com.moczul.ok2curl.CurlInterceptor
import okhttp3.OkHttpClient

class AuthenticationProvider(
    cookieJar: ReadOnlyCookieJar = CookieJar(DefaultCookieJar()),
    client: OkHttpClient =
        OkHttpClient.Builder()
            .addNetworkInterceptor(CurlInterceptor { message -> println("$message\n\r") })
            .cookieJar(cookieJar)
            .build(),
    jwtTokenFactory: JWTTokenFactory = VTMGOJWTTokenFactory(client, cookieJar),
    profileRepo: ProfileRepo = HttpProfileRepo(client, JsonProfileParser(), HeaderBuilder()),
) : JWTTokenFactory by jwtTokenFactory,
    ProfileRepo by profileRepo

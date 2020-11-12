package be.tapped.vtmgo.authentication

import be.tapped.vtmgo.common.ReadOnlyCookieJar
import com.moczul.ok2curl.CurlInterceptor
import okhttp3.OkHttpClient

class AuthenticationProvider(
    cookieJar: ReadOnlyCookieJar = CookieJar(),
    client: OkHttpClient =
        OkHttpClient.Builder()
            .addNetworkInterceptor(CurlInterceptor { message -> println("$message\n\r") })
            .cookieJar(cookieJar)
            .build(),
    jwtTokenFactory: JWTTokenFactory = VTMGOJWTTokenFactory(client, cookieJar),
    profileRepo: ProfileRepo = HttpProfileRepo(client)
) : JWTTokenFactory by jwtTokenFactory,
    ProfileRepo by profileRepo

package be.tapped.vtmgo.common

import com.moczul.ok2curl.CurlInterceptor
import okhttp3.OkHttpClient

internal val defaultOkHttpClient =
    OkHttpClient.Builder()
        .cookieJar(defaultCookieJar)
        .addNetworkInterceptor(CurlInterceptor { message -> println("$message\n\r") })
        .build()

package be.tapped.vrtnu.common

import com.moczul.ok2curl.CurlInterceptor
import okhttp3.OkHttpClient

internal val defaultOkHttpClient =
    OkHttpClient.Builder()
        .addNetworkInterceptor(CurlInterceptor { message -> println("$message\n\r") })
        .build()

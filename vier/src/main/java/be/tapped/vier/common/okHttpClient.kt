package be.tapped.vier.common

import com.moczul.ok2curl.CurlInterceptor
import okhttp3.OkHttpClient

private val defaultCurlInterceptor = CurlInterceptor { message -> println("$message\n\r") }

internal val vierApiDefaultOkHttpClient = OkHttpClient.Builder().addNetworkInterceptor(defaultCurlInterceptor).build()

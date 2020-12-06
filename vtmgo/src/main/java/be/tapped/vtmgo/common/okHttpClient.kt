package be.tapped.vtmgo.common

import com.moczul.ok2curl.CurlInterceptor
import okhttp3.OkHttpClient

private val defaultCurlInterceptor = CurlInterceptor { message -> println("$message\n\r") }

internal val vtmApiDefaultOkHttpClient = OkHttpClient.Builder().cookieJar(defaultCookieJar).addNetworkInterceptor(defaultCurlInterceptor).build()
internal val anvatoDefaultOkHttpClient = OkHttpClient.Builder().addNetworkInterceptor(defaultCurlInterceptor).build()

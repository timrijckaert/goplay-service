package be.tapped.goplay.common

import okhttp3.OkHttpClient

@Deprecated("OkHttp3 is a JVM only library. We want to go full MPP. Therefore switch to use Ktor client", ReplaceWith("ktorClient", "be.tapped.goplay.common"))
internal val goPlayApiDefaultOkHttpClient = OkHttpClient.Builder().build()

package be.tapped.vtmgo.common

import okhttp3.OkHttpClient

internal val vtmApiDefaultOkHttpClient = OkHttpClient.Builder().cookieJar(defaultCookieJar).build()
internal val anvatoDefaultOkHttpClient = OkHttpClient.Builder().build()

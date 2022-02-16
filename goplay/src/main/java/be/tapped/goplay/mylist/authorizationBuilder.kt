package be.tapped.goplay.mylist

import be.tapped.goplay.profile.IdToken
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.headers
import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpHeaders

internal fun HttpRequestBuilder.defaultAuthorizationHeader(idToken: IdToken): HeadersBuilder =
    headers {
        append(HttpHeaders.Authorization, "Bearer ${idToken.token}")
    }

package be.tapped.goplay

import arrow.core.Either
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.statement.*
import java.nio.charset.Charset

public suspend inline fun <reified T> HttpClient.safeGet(
    urlString: String,
    block: HttpRequestBuilder.() -> Unit = {}
): Either<ApiResponse.Failure.Network, T> =
    Either.catch { get<T>(urlString, block) }.mapLeft(ApiResponse.Failure::Network)

public suspend fun HttpResponse.safeReadText(fallbackCharset: Charset? = null): Either<ApiResponse.Failure.Network, String> =
    Either.catch { readText(fallbackCharset) }.mapLeft(ApiResponse.Failure::Network)

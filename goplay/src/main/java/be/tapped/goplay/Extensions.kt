package be.tapped.goplay

import arrow.core.Either
import arrow.core.Nel
import arrow.core.NonEmptyList
import arrow.core.rightIfNotNull
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import java.nio.charset.Charset

internal inline fun <A, T> List<T>.toNel(default: () -> A): Either<A, Nel<T>> = NonEmptyList.fromList(this).orNull().rightIfNotNull(default)

public suspend inline fun <reified T> HttpClient.safeGet(
    urlString: String,
    block: HttpRequestBuilder.() -> Unit = {}
): Either<Failure.Network, T> =
    Either.catch { get<T>(urlString, block) }.mapLeft(Failure::Network)

public suspend fun HttpResponse.safeReadText(fallbackCharset: Charset? = null): Either<Failure.Network, String> =
    Either.catch { readText(fallbackCharset) }.mapLeft(Failure::Network)

public inline fun <reified T> Json.safeDecodeFromString(string: String): Either<Failure.JsonParsingException, T> =
    Either.catch { decodeFromString<T>(string) }.mapLeft(Failure::JsonParsingException)

public inline fun <reified T> Json.safeDecodeFromJsonElement(json: JsonElement): Either<Failure.JsonParsingException, T> =
    Either.catch { decodeFromJsonElement<T>(json) }.mapLeft(Failure::JsonParsingException)

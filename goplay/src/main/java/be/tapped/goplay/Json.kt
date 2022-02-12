package be.tapped.goplay

import arrow.core.Either
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

public inline fun <reified T> Json.safeDecodeFromString(string: String): Either<ApiResponse.Failure.JsonParsingException, T> =
    Either.catch { decodeFromString<T>(string) }.mapLeft(ApiResponse.Failure::JsonParsingException)

public inline fun <reified T> Json.safeDecodeFromJsonElement(json: JsonElement): Either<ApiResponse.Failure.JsonParsingException, T> =
    Either.catch { decodeFromJsonElement<T>(json) }.mapLeft(ApiResponse.Failure::JsonParsingException)

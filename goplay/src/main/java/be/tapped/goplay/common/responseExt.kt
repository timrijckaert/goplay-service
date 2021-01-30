package be.tapped.goplay.common

import arrow.core.Either
import arrow.core.computations.either
import be.tapped.common.internal.validateResponse
import be.tapped.goplay.ApiResponse
import okhttp3.Response

@Suppress("BlockingMethodInNonBlockingContext") //False positive
public suspend fun Response.safeBodyString(): Either<ApiResponse.Failure, String> = either {
    !validateResponse { ApiResponse.Failure.NetworkFailure(code, request) }
    !Either.fromNullable(body?.string()).mapLeft { ApiResponse.Failure.HTML.EmptyHTML }
}

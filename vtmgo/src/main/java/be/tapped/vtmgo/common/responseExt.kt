package be.tapped.vtmgo.common

import arrow.core.Either
import arrow.core.computations.either
import be.tapped.common.internal.validateResponse
import be.tapped.vtmgo.ApiResponse
import okhttp3.Response

@Suppress("BlockingMethodInNonBlockingContext") //False positive
public suspend fun Response.safeBodyString(): Either<ApiResponse.Failure, String> = either {
    validateResponse { ApiResponse.Failure.NetworkFailure(code, request) }.bind()
    Either.fromNullable(body?.string()).mapLeft { ApiResponse.Failure.EmptyJson }.bind()
}

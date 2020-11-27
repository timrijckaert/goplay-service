package be.tapped.vrtnu.common

import arrow.core.Either
import arrow.core.computations.either
import be.tapped.common.validateResponse
import be.tapped.vrtnu.ApiResponse
import okhttp3.Response

@Suppress("BlockingMethodInNonBlockingContext") //False positive
suspend fun Response.safeBodyString(): Either<ApiResponse.Failure, String> =
    either {
        !validateResponse { ApiResponse.Failure.NetworkFailure(code, request) }
        !Either.fromNullable(body?.string()).mapLeft { ApiResponse.Failure.EmptyJson }
    }

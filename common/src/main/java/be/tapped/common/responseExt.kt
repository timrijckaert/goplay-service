package be.tapped.common

import arrow.core.Either
import okhttp3.Response

fun <L> Response.validateResponse(ifFalse: () -> L): Either<L, Response> =
    Either.conditionally(isSuccessful, ifFalse, { this })

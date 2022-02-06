package be.tapped.common.internal

import arrow.core.Either
import okhttp3.Response

public fun <L> Response.validateResponse(ifFalse: () -> L): Either<L, Response> = Either.conditionally(isSuccessful, ifFalse, { this })

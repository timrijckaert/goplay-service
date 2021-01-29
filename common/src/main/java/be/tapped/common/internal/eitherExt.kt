package be.tapped.common.internal

import arrow.core.*

public fun <T, R> Either<T, R>.toValidateNel(): Validated<NonEmptyList<T>, R> = when (this) {
    is Either.Left -> a.invalidNel()
    is Either.Right -> b.validNel()
}

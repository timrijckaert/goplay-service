package be.tapped.goplay

import arrow.core.Either
import arrow.core.Nel
import arrow.core.NonEmptyList
import arrow.core.rightIfNotNull

internal inline fun <A, T> List<T>.toNel(default: () -> A): Either<A, Nel<T>> = NonEmptyList.fromList(this).orNull().rightIfNotNull(default)

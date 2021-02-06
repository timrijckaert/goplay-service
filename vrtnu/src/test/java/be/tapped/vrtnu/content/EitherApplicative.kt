package be.tapped.vrtnu.content

import arrow.core.Either
import arrow.core.extensions.either.applicative.applicative
import arrow.core.fix
import arrow.core.right
import arrow.core.sequenceEither
import io.kotest.core.spec.style.StringSpec

public class EitherApplicative : StringSpec({
    "combine Eithers in one Either with list" {
        val either1: Either<Exception, Int> = 1.right().mapLeft { Exception() }
        val either2: Either<Exception, Int> = 2.right().mapLeft { Exception() }
        //val a: List<Either<Exception, Int>> = listOf(either1, either2)

        val eitherApplicative = Either.applicative<Exception>()
        val c: Either<Exception, List<Int>> = eitherApplicative.mapN(either1, either2) {
            listOf(it.a, it.b)
        }.fix()

        val a: Either<Nothing, List<Int>> = listOf(1.right(), 2.right()).sequenceEither()
    }
})

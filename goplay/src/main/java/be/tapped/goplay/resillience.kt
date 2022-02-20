package be.tapped.goplay

import arrow.core.Either
import arrow.core.Nel
import arrow.core.flatten
import arrow.fx.coroutines.Schedule.Companion.exponential
import arrow.fx.coroutines.Schedule.Companion.identity
import arrow.fx.coroutines.Schedule.Companion.recurs
import arrow.fx.coroutines.retry
import be.tapped.goplay.content.Category
import be.tapped.goplay.content.Program
import be.tapped.goplay.content.ProgramRepo
import be.tapped.goplay.epg.GoPlayBrand
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.runBlocking
import arrow.fx.coroutines.CircuitBreaker as FxCircuitBreaker
import arrow.fx.coroutines.Schedule as FxSchedule

public interface Resilience {
    public suspend fun <A> resilient(action: suspend () -> A): A

    public suspend fun <A> resilientCatch(action: suspend () -> A): Either<Throwable, A> =
        Either.catch { resilient(action) }
}

@OptIn(ExperimentalTime::class)
public data class ResilientConfig(
    val circuitBreaker: CircuitBreaker = CircuitBreaker(),
    val schedule: Schedule = Schedule(),
) {

    public fun toResilience(): Resilience {
        val breaker = runBlocking { toCircuitBreaker() }
        return object : Resilience {
            override suspend fun <A> resilient(action: suspend () -> A): A =
                toSchedule<Throwable>().retry {
                    breaker.protectOrThrow(action)
                }
        }
    }

    private suspend fun toCircuitBreaker(): FxCircuitBreaker = FxCircuitBreaker.of(
        maxFailures = circuitBreaker.failureRateThreshold,
        resetTimeoutNanos = circuitBreaker.durationOfOpenState.inWholeNanoseconds.toDouble()
    )

    private fun <In> toSchedule(): FxSchedule<In, In> =
        (recurs<In>(schedule.maxRetries) and exponential(
            schedule.retryWaitDuration,
            schedule.retryBackoffMultiplier
        )) zipRight identity()

    public data class CircuitBreaker(val failureRateThreshold: Int = 10, val durationOfOpenState: Duration = 4.seconds)

    public data class Schedule(
        val retryWaitDuration: Duration = 500.milliseconds,
        val maxRetries: Int = 4,
        val retryBackoffMultiplier: Double = 2.5,
    )
}

internal fun ProgramRepo.withResilience(resilience: Resilience): ProgramRepo =
    object : ProgramRepo {
        override suspend fun fetchPrograms(): Either<Failure, AllPrograms> =
            resilience.resilientCatch(this@withResilience::fetchPrograms).mapLeft(Failure::Network).flatten()

        override suspend fun fetchProgramByLink(link: Program.Link): Either<Failure, Detail> =
            resilience.resilientCatch {
                this@withResilience.fetchProgramByLink(link)
            }.mapLeft(Failure::Network).flatten()

        override suspend fun fetchProgramById(id: Program.Id): Either<Failure, Detail> =
            resilience.resilientCatch {
                this@withResilience.fetchProgramById(id)
            }.mapLeft(Failure::Network).flatten()

        override suspend fun fetchPopularPrograms(brand: GoPlayBrand?): Either<Failure, Nel<Detail>> =
            resilience.resilientCatch {
                this@withResilience.fetchPopularPrograms(brand)
            }.mapLeft(Failure::Network).flatten()

        override suspend fun fetchProgramsByCategory(categoryId: Category.Id): Either<Failure, Nel<Detail>> =
            resilience.resilientCatch {
                this@withResilience.fetchProgramsByCategory(categoryId)
            }.mapLeft(Failure::Network).flatten()
    }

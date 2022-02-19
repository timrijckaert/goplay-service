package be.tapped.goplay

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class IosCoroutineDispatchers : CoroutineDispatchers {

    override val main: CoroutineDispatcher =
        try {
            Dispatchers.Main
        } catch (exception: IllegalStateException) {
            Dispatchers.Default
        }

    override val default: CoroutineDispatcher = Dispatchers.Default

    override val io: CoroutineDispatcher = Dispatchers.Default

    override val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
}

internal actual val dispatchers: CoroutineDispatchers = IosCoroutineDispatchers()

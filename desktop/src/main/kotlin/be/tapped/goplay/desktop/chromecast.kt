package be.tapped.goplay.desktop

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import su.litvak.chromecast.api.v2.ChromeCast
import su.litvak.chromecast.api.v2.ChromeCasts
import su.litvak.chromecast.api.v2.ChromeCastsListener
import java.net.InetAddress
import kotlin.time.Duration.Companion.seconds

public fun discovery(inetAddress: InetAddress): Flow<List<ChromeCast>> = callbackFlow {
    val listener = object : ChromeCastsListener {
        override fun newChromeCastDiscovered(chromeCast: ChromeCast?) {
            launch { send(ChromeCasts.get()) }
        }

        override fun chromeCastRemoved(chromeCast: ChromeCast?) {
            launch { send(ChromeCasts.get()) }
        }
    }

    ChromeCasts.registerListener(listener)
    ChromeCasts.startDiscovery(inetAddress)

    awaitClose {
        ChromeCasts.unregisterListener(listener)
        ChromeCasts.stopDiscovery()
    }
}.flowOn(Dispatchers.IO)

public suspend fun example() {
    val cast = discovery(
        // Ip-address of MacOs WiFi Adapter.
        InetAddress.getByAddress(byteArrayOf(192.toByte(), 168.toByte(), 0, 246.toByte()))
    ).firstOrNull()?.firstOrNull()
    cast?.run {
        try {
            launchApp("CC1AD845")
            load(
                "Big Buck Bunny",           // Media title
                "images/BigBuckBunny.jpg",  // URL to thumbnail based on media URL
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", // media URL
                null // media content type (optional, will be discovered automatically)
            )

//            delay(status.media.duration.seconds)
            delay(5.seconds)
        } finally {
            stopApp()
            disconnect()
        }
    } ?: throw RuntimeException("No chromecast found")
}

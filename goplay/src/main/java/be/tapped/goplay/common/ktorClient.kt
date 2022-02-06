package be.tapped.goplay.common

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.logging.Logging

internal val ktorClient: HttpClient = HttpClient(Apache) {
    install(Logging)
}

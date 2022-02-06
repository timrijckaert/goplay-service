package be.tapped.goplay.common

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer

internal val ktorClient: HttpClient = HttpClient(Apache) {
    install(JsonFeature)
    install(JsonFeature) {
        serializer = KotlinxSerializer(jsonSerializer)
    }
}

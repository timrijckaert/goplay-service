package be.tapped.goplay.common

import kotlinx.serialization.json.Json

internal val jsonSerializer =
    Json {
        isLenient = true
        ignoreUnknownKeys = true
    }

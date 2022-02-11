package be.tapped.goplay.content

import be.tapped.goplay.epg.EpgRepo
import be.tapped.goplay.epg.httpEpgRepo
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import kotlinx.serialization.json.Json

internal const val siteUrl: String = "https://www.goplay.be"
internal const val apiVierVijfZes: String = "https://api.viervijfzes.be"
internal const val apiGoPlay: String = "https://api.goplay.be"

internal val jsonSerializer =
    Json {
        isLenient = true
        ignoreUnknownKeys = true
    }

internal val httpClient: HttpClient =
    HttpClient(Apache) {
        install(JsonFeature) {
            serializer = KotlinxSerializer(jsonSerializer)
        }
    }

public object GoPlayApi :
    ProgramRepo by httpProgramRepo(httpClient, HtmlProgramParser(jsonSerializer)),
    EpgRepo by httpEpgRepo(httpClient)

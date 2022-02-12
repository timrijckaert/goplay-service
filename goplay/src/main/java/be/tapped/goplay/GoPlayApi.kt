package be.tapped.goplay

import be.tapped.goplay.content.AllProgramsHtmlJsonExtractor
import be.tapped.goplay.content.HttpProgramRepo
import be.tapped.goplay.content.ProgramDetailHtmlJsonExtractor
import be.tapped.goplay.content.ProgramRepo
import be.tapped.goplay.epg.EpgRepo
import be.tapped.goplay.epg.httpEpgRepo
import be.tapped.goplay.profile.HttpProfileRepo
import be.tapped.goplay.profile.ProfileRepo
import be.tapped.goplay.profile.ProfileUserAttributeParser
import be.tapped.goplay.stream.StreamRepo
import be.tapped.goplay.stream.httpStreamRepo
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
    ProgramRepo by HttpProgramRepo(httpClient, jsonSerializer, AllProgramsHtmlJsonExtractor(), ProgramDetailHtmlJsonExtractor()),
    EpgRepo by httpEpgRepo(httpClient),
    StreamRepo by httpStreamRepo(httpClient),
    ProfileRepo by HttpProfileRepo(ProfileUserAttributeParser())

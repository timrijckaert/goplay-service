package be.tapped.goplay

import be.tapped.goplay.content.AllProgramsHtmlJsonExtractor
import be.tapped.goplay.content.CategoryRepo
import be.tapped.goplay.content.HttpProgramRepo
import be.tapped.goplay.content.ProgramDetailHtmlJsonExtractor
import be.tapped.goplay.content.ProgramRepo
import be.tapped.goplay.content.categoryRepo
import be.tapped.goplay.content.contentRootRepo
import be.tapped.goplay.content.contentTreeJsonParser
import be.tapped.goplay.epg.EpgRepo
import be.tapped.goplay.epg.httpEpgRepo
import be.tapped.goplay.mylist.HttpMyListRepo
import be.tapped.goplay.mylist.MyListRepo
import be.tapped.goplay.mylist.addFavoriteProgramRepo
import be.tapped.goplay.mylist.myFavoriteProgramRepo
import be.tapped.goplay.mylist.removeFavoriteRepo
import be.tapped.goplay.profile.HttpProfileRepo
import be.tapped.goplay.profile.ProfileRepo
import be.tapped.goplay.profile.ProfileUserAttributeParser
import be.tapped.goplay.stream.StreamRepo
import be.tapped.goplay.stream.hlsStreamResolver
import be.tapped.goplay.stream.httpStreamRepo
import be.tapped.goplay.stream.mpegDashStreamResolver
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
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
    ProgramRepo by HttpProgramRepo(
        httpClient,
        jsonSerializer,
        AllProgramsHtmlJsonExtractor(),
        ProgramDetailHtmlJsonExtractor(),
        contentRootRepo(httpClient, contentTreeJsonParser())
    ).withResilience(ResilientConfig().toResilience()),
    EpgRepo by httpEpgRepo(httpClient),
    StreamRepo by httpStreamRepo(httpClient, mpegDashStreamResolver(httpClient), hlsStreamResolver()),
    ProfileRepo by HttpProfileRepo(ProfileUserAttributeParser()),
    CategoryRepo by categoryRepo(contentRootRepo(httpClient, contentTreeJsonParser())),
    MyListRepo by HttpMyListRepo(
        myFavoriteProgramRepo(httpClient),
        addFavoriteProgramRepo(httpClient),
        removeFavoriteRepo(httpClient)
    )

package com.example.sample

import arrow.core.Tuple2
import arrow.core.toT
import be.tapped.vier.ApiResponse
import be.tapped.vier.content.EpisodeUuid
import be.tapped.vier.content.SearchHit
import be.tapped.vier.content.VideoUuid
import be.tapped.vier.content.VierApi
import be.tapped.vier.epg.HttpEpgRepo
import be.tapped.vier.profile.HttpProfileRepo

public suspend fun main(args: Array<String>) {
    val userName = args[0]
    val password = args[1]

    val (token, profile) = authentication(userName, password)
    println(token)
    println(profile)

    api(token)

    epg()
}

private suspend fun api(token: ApiResponse.Success.Authentication.Token) {
    val vierApi = VierApi()

    // All Programs
    val fetchPrograms = vierApi.fetchPrograms()
    val programs = fetchPrograms.orNull()!!
    println(programs)

    // Program by URL
    val deSlimsteMensTerWereldByProgramUrl =
        vierApi.fetchProgram(SearchHit.Source.SearchKey.Program("https://www.goplay.be/de-slimste-mens-ter-wereld"))
    println(deSlimsteMensTerWereldByProgramUrl)

    // Search
    val deSlimsteMensSearchQuery = vierApi.search("de slimste mens ter wereld")
    println(deSlimsteMensSearchQuery)

    // Episode by URL
    val deSlimsteMensS18E36ByUrl = vierApi.fetchEpisode(
        SearchHit.Source.SearchKey.EpisodeByNodeId(
            "35399",
            "https://www.vier.be/video/de-slimste-mens-ter-wereld/de-slimste-mens-ter-wereld-s18/de-slimste-mens-ter-wereld-s18-aflevering-36"
        )
    )
    println(deSlimsteMensS18E36ByUrl)

    // Episode by Episode UUID
    val episode = vierApi.fetchEpisode(EpisodeUuid("c4e8d653-224a-4985-95ac-c8360074c518"))
    println(episode)

    // Stream
    val s18e36 = VideoUuid("26ab85f9-3946-4e1b-8b3f-79018252acb0")
    val deSlimsteMensS18E36Stream = vierApi.streamByVideoUuid(token.token.idToken, s18e36)
    println(deSlimsteMensS18E36Stream)
}

private suspend fun authentication(
    userName: String,
    password: String,
): Tuple2<ApiResponse.Success.Authentication.Token, ApiResponse.Success.Authentication.Profile> {
    val httpProfileRepo = HttpProfileRepo()
    val token = httpProfileRepo.fetchTokens(userName, password).orNull()!!
    // Assert that the new tokens are able to be fetched
    httpProfileRepo.refreshTokens(token.token.refreshToken).orNull()!!

    val profile = httpProfileRepo.getUserAttributes(token.token.accessToken).orNull()!!
    return token toT profile
}

private suspend fun epg() {
    val epgRepo = HttpEpgRepo()
    val todayEpg = epgRepo.epg()
    println(todayEpg)
}

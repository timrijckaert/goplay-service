package com.example.sample

import arrow.core.Tuple2
import arrow.core.toT
import be.tapped.vier.ApiResponse
import be.tapped.vier.content.SearchHit
import be.tapped.vier.content.VideoUuid
import be.tapped.vier.content.VierApi
import be.tapped.vier.profile.HttpProfileRepo

public suspend fun main(args: Array<String>) {
    val userName = args[0]
    val password = args[1]

    val (token, profile) = authentication(userName, password)
    println(token)
    println(profile)

    api(token)
}

private suspend fun api(token: ApiResponse.Success.Authentication.Token) {
    val vierApi = VierApi()

    // All Programs
    val programs = vierApi.fetchPrograms().orNull()!!
    println(programs)

    // Program by URL
    val deSlimsteMensTerWereldByProgramUrl = vierApi.fetchProgram(SearchHit.Source.SearchKey.Program("https://www.vier.be/de-slimste-mens-ter-wereld"))
    println(deSlimsteMensTerWereldByProgramUrl)

    // Search
    val deSlimsteMensSearchQuery = vierApi.search("de slimste mens ter wereld")
    println(deSlimsteMensSearchQuery)

    // Stream
    val s18e36 = VideoUuid("26ab85f9-3946-4e1b-8b3f-79018252acb0")
    val deSlimsteMensS18E36Stream = vierApi.streamForEpisodeVideoUuid(token.idToken, s18e36)
    println(deSlimsteMensS18E36Stream)
}

private suspend fun authentication(
    userName: String,
    password: String,
): Tuple2<ApiResponse.Success.Authentication.Token, ApiResponse.Success.Authentication.Profile> {
    val httpProfileRepo = HttpProfileRepo()
    val token = httpProfileRepo.fetchTokens(userName, password).orNull()!!
    // Assert that the new tokens are able to be fetched
    httpProfileRepo.refreshTokens(token.refreshToken).orNull()!!

    val profile = httpProfileRepo.getUserAttributes(token.accessToken).orNull()!!
    return token toT profile
}

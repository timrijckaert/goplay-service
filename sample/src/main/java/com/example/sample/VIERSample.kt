package com.example.sample

import arrow.core.Tuple2
import arrow.core.toT
import be.tapped.vier.ApiResponse
import be.tapped.vier.VierAPI
import be.tapped.vier.content.VierApi
import be.tapped.vier.profile.HttpProfileRepo

public suspend fun main(args: Array<String>) {
    val userName = args[0]
    val password = args[1]

    // val (token, profile) = authentication(userName, password)
    // println(token)
    // println(profile)

    api()
}

private suspend fun api() {
    val vierApi = VierApi()

    // Programs
    val programs = vierApi.fetchPrograms().orNull()!!
    println(programs)
}

private suspend fun authentication(userName: String, password: String): Tuple2<ApiResponse.Success.Authentication.Token, ApiResponse.Success.Authentication.Profile> {
    val httpProfileRepo = HttpProfileRepo()
    val token = httpProfileRepo.fetchTokens(userName, password).orNull()!!
    // Assert that the new tokens are able to be fetched
    httpProfileRepo.refreshTokens(token.refreshToken).orNull()!!

    val profile = httpProfileRepo.getUserAttributes(token.accessToken).orNull()!!
    return token toT profile
}

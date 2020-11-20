package com.example.sample

import be.tapped.vrtnu.authentication.AuthenticationProvider
import be.tapped.vrtnu.content.ElasticSearchQueryBuilder
import be.tapped.vrtnu.content.VRTApi
import kotlinx.coroutines.flow.toList

suspend fun main(args: Array<String>) {
    val userName = args[0]
    val password = args[1]
    val authenticationProvider = AuthenticationProvider()

    // Authentication
    authentication(authenticationProvider, userName, password)

    // API
    apiSamples()
}

private suspend fun authentication(authenticationProvider: AuthenticationProvider, userName: String, password: String) {
    val tokenWrapperResult = authenticationProvider.fetchTokenWrapper(userName, password)
    val refreshToken = tokenWrapperResult.orNull()!!.tokenWrapper.refreshToken
    val newTokenWrapperResult = authenticationProvider.refreshTokenWrapper(refreshToken)
    val xVRTToken = authenticationProvider.fetchXVRTToken(userName, password)
    val vrtPlayerToken = authenticationProvider.fetchVRTPlayerToken(xVRTToken.orNull()!!.xVRTToken)
}

private suspend fun apiSamples() {
    val vrtApi = VRTApi()
    // A-Z
    val azPrograms = vrtApi.fetchAZPrograms()
    println(azPrograms)

    // Categories
    val categories = vrtApi.fetchCategories()
    println(categories)

    // Search
    val allSearchResults = vrtApi.episodes(ElasticSearchQueryBuilder.SearchQuery(category = "cultuur")).toList()
    println(allSearchResults)

    // Single Program
    val programName = "Geubels en de Hollanders"
    val geubelsEnDeHollanders = vrtApi.fetchProgramByName(programName).map { vrtApi.episodesForProgram(it.program).toList() }
    println(geubelsEnDeHollanders)
}

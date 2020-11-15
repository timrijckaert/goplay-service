package com.example.sample

import arrow.core.Either
import be.tapped.vrtnu.authentication.AuthenticationProvider
import be.tapped.vrtnu.content.ApiResponse
import be.tapped.vrtnu.content.ElasticSearchRepo
import be.tapped.vrtnu.content.VRTApi
import kotlinx.coroutines.flow.toList

suspend fun main(args: Array<String>) {
    val userName = args[0]
    val password = args[1]
    val authenticationProvider = AuthenticationProvider()

    // Authentication
    val tokenWrapperResult = authenticationProvider.fetchTokenWrapper(userName, password)
    val refreshToken = tokenWrapperResult.orNull()!!.tokenWrapper.refreshToken
    val newTokenWrapperResult = authenticationProvider.refreshTokenWrapper(refreshToken)

    // API
    val vrtApi = VRTApi()

    //// A-Z
    val azPrograms = vrtApi.fetchAZPrograms()
    println(azPrograms)

    //// Categories
    val categories = vrtApi.fetchCategories()
    println(categories)

    //// Search
    val allSearchResults = mutableListOf<Either<ApiResponse.Failure, ApiResponse.Success.Episodes>>()
    val episodesFromSearch = vrtApi.search(ElasticSearchRepo.SearchQuery(category = "cultuur")).toList(allSearchResults)
    println(allSearchResults)
}

package com.example.sample

import be.tapped.vrtnu.authentication.AuthenticationProvider
import be.tapped.vrtnu.content.SearchQuery
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
    with(VRTApi()) {
        // A-Z
        val azPrograms = fetchAZPrograms()
        println(azPrograms)

        // Categories
        val categories = fetchCategories()
        println(categories)

        // Search
        val allSearchResults = search(SearchQuery(category = "cultuur")).toList()
        println(allSearchResults)

        // Single Program
        val geubelsEnDeHollanders = fetchProgramByName("Geubels en de Hollanders")
        println(geubelsEnDeHollanders)
    }
}

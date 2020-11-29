package com.example.sample

import be.tapped.vtmgo.epg.HttpEpgRepo
import be.tapped.vtmgo.content.StoreFrontType
import be.tapped.vtmgo.content.VTMApi
import be.tapped.vtmgo.profile.JWT
import be.tapped.vtmgo.profile.Profile
import be.tapped.vtmgo.profile.ProfileRepo

suspend fun main(args: Array<String>) {
    val userName = args[0]
    val password = args[1]

    // Authentication
    val (token, profile) = auth(userName, password)

    // Content
    api(token, profile)

    // EPG
    epg()
}

private suspend fun auth(userName: String, password: String): Pair<JWT, Profile> {
    val profileRepo = ProfileRepo()

    val jwtToken = profileRepo.login(userName, password)
    println(jwtToken)

    val token = jwtToken.orNull()!!.jwt
    val profiles = profileRepo.getProfiles(token)
    println("Fetched profiles=$profiles")

    val profile: Profile = profiles.first()
    return token to profile
}

private suspend fun api(jwtToken: JWT, profile: Profile) {
    val vtmApi = VTMApi()

    // Programs
    val catalogForChosenVtmGoProduct = vtmApi.fetchAZ(jwtToken, profile)
    val productTypeWithCatalog = catalogForChosenVtmGoProduct.orNull()!!.catalog.groupBy { it.target::class.java }
    println(productTypeWithCatalog)

    // Categories
    val categories = vtmApi.fetchCategories(jwtToken, profile)
    println(categories)

    // Live Channels
    val liveChannels = vtmApi.fetchChannels(jwtToken, profile)
    println(liveChannels)

    // Store front
    val series = vtmApi.fetchStoreFront(jwtToken, profile, StoreFrontType.MAIN)
    println(series)

    // Favorites
    val myFavorites = vtmApi.fetchMyFavorites(jwtToken, profile)
    println(myFavorites)

    // Search
    val searchResult = vtmApi.search(jwtToken, profile, "Code van Coppens")
    println(searchResult)
}

private suspend fun epg() {
    val httpEpgRepo = HttpEpgRepo()
    val todayEpg = httpEpgRepo.epg()
    println(todayEpg)
}

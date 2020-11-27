package com.example.sample

import be.tapped.vtmgo.content.VTMApi
import be.tapped.vtmgo.profile.JWT
import be.tapped.vtmgo.profile.Profile
import be.tapped.vtmgo.profile.ProfileRepo

suspend fun main(args: Array<String>) {
    val userName = args[0]
    val password = args[1]
    val profileRepo = ProfileRepo()

    val jwtToken = profileRepo.login(userName, password)
    println(jwtToken)

    val token = jwtToken.orNull()!!.jwt
    val profiles = profileRepo.getProfiles(token)
    println("Fetched profiles=$profiles")

    val profile: Profile = profiles.first()

    api(token, profile)
}

private suspend fun api(token: JWT, profile: Profile) {
    val vtmApi = VTMApi()

    // Programs
    val catalogForChosenVtmGoProduct = vtmApi.fetchAZ(token, profile).orNull()!!.catalog
    val productTypeWithCatalog = catalogForChosenVtmGoProduct.groupBy { it.target.type }
    println(productTypeWithCatalog)

    // Categories
    val categories = vtmApi.fetchCategories(token, profile)
    println(categories)

    // Live Channels
    val liveChannels = vtmApi.fetchChannels(token, profile)
    println(liveChannels)

}

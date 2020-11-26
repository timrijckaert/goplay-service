package com.example.sample

import be.tapped.vtmgo.content.VTMApi
import be.tapped.vtmgo.profile.Profile
import be.tapped.vtmgo.profile.ProfileRepo

suspend fun main(args: Array<String>) {
    val userName = args[0]
    val password = args[1]
    val profileRepo = ProfileRepo()

    val jwtToken = profileRepo.login(userName, password)
    println(jwtToken)

    val token = jwtToken.orNull()!!
    val profiles = profileRepo.getProfiles(token)
    println("Fetched profiles=$profiles")

    val profile: Profile = profiles.first()

    val vtmApi = VTMApi()
    val programsForChosenVtmGoProduct = vtmApi.fetchAZPrograms(token, profile)
    val categories = vtmApi.fetchCategories(token, profile)
    println(programsForChosenVtmGoProduct)
    println(categories)
}

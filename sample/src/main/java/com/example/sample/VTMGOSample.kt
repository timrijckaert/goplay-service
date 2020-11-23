package com.example.sample

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
}

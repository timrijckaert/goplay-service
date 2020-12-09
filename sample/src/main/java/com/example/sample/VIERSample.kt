package com.example.sample

import be.tapped.vier.VierAPI
import be.tapped.vier.profile.HttpProfileRepo

public suspend fun main(args: Array<String>) {
    val userName = args[0]
    val password = args[1]
    val httpProfileRepo = HttpProfileRepo()
    val token = httpProfileRepo.fetchTokens(userName, password).orNull()!!
    val newTokens = httpProfileRepo.refreshTokens(token.refreshToken)

    val profile = httpProfileRepo.getUserAttributes(token.accessToken)

    val vierApi = VierAPI()
    val contentTree = vierApi.getContentTree(token.accessToken)
}

package com.example.sample

import be.tapped.vier.VierAPI
import be.tapped.vier.authentication.VierTokenProvider

public fun main(args: Array<String>) {
    val userName = args[0]
    val password = args[1]
    val tokenProvider = VierTokenProvider()
    val tokens = tokenProvider.getAuthenticationResultType(userName, password)
    val userAttributes = tokenProvider.getUserAttributes(tokens.accessToken())
    val newTokens = tokenProvider.refreshToken(tokens.refreshToken())

    val vierApi = VierAPI()
    val contentTree = vierApi.getContentTree(newTokens.accessToken())
}

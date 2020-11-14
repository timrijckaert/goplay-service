package com.example.sample

import be.tapped.vrtnu.authentication.AuthenticationProvider

suspend fun main(args: Array<String>) {
    val userName = args[0]
    val password = args[1]
    val authenticationProvider = AuthenticationProvider()

    val tokenWrapperResult = authenticationProvider.fetchTokenWrapper(userName, password)
    val refreshToken = tokenWrapperResult.orNull()!!.tokenWrapper.refreshToken
    val newTokenWrapperResult = authenticationProvider.refreshTokenWrapper(refreshToken)
}

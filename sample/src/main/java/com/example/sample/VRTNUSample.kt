package com.example.sample

import be.tapped.vrtnu.authentication.AuthenticationProvider

suspend fun main(args: Array<String>) {
    val userName = args[0]
    val password = args[1]
    val tokenResolver = AuthenticationProvider()
    val tokenWrapperResult = tokenResolver.fetchTokenWrapper(userName, password)
    println(tokenWrapperResult)
}

package be.tapped.vtmgo

import be.tapped.vtmgo.authentication.AuthenticationProvider

suspend fun main(args: Array<String>) {
    val userName = args[0]
    val password = args[1]
    val tokenProvider = AuthenticationProvider()

    val jwtToken = tokenProvider.login(userName, password)
    println(jwtToken)

    jwtToken.orNull()?.let { token ->
        val profiles = tokenProvider.getProfiles(token)
        println("Fetched profiles=$profiles")
    }
}

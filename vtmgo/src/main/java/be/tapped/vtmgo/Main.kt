package be.tapped.vtmgo

import be.tapped.vtmgo.authentication.VTMTokenProvider

suspend fun main(args: Array<String>) {
    val userName = args[0]
    val password = args[1]
    val tokenProvider = VTMTokenProvider()

    val jwtToken = tokenProvider.login(userName, password).orNull()
    jwtToken?.let { token ->
        val profiles = tokenProvider.getProfiles(token)
        println("Fetched profiles=$profiles")
    }
}

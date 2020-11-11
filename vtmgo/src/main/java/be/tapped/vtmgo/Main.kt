package be.tapped.vtmgo

import be.tapped.vtmgo.authentication.VTMTokenProvider

fun main(args: Array<String>) {
    val userName = args[0]
    val password = args[1]
    val tokenProvider = VTMTokenProvider()
    tokenProvider.login(userName, password)
}

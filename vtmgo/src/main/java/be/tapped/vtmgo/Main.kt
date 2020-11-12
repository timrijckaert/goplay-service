package be.tapped.vtmgo

import be.tapped.vtmgo.authentication.VTMTokenProvider
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {
    val userName = args[0]
    val password = args[1]
    val tokenProvider = VTMTokenProvider()
    runBlocking {
        println(tokenProvider.login(userName, password))
    }
}

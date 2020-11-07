package be.tapped.vrtnu

import be.tapped.vrtnu.model.VRTLogin

fun main(args: Array<String>) {
    val userName = args[0]
    val password = args[1]
    val tokenResolver = TokenResolver(object : TokenResolver.Listener {
        override fun onFailedToLogin(failure: VRTLogin.Failure) {

        }
    })
    tokenResolver.login(userName, password)
}

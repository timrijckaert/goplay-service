package be.tapped.vier

import be.tapped.vier.authentication.VierTokenProvider

fun main(args: Array<String>) {
    val userName = args[0]
    val password = args[1]
    val tokenProvider = VierTokenProvider()
    val tokens = tokenProvider.getAuthenticationResultType(userName, password)
    val userAttributes = tokenProvider.getUserAttributes(tokens.accessToken())
    val newTokens = tokenProvider.refreshToken(tokens.refreshToken())

    val vierApi = VierAPI()
    val contentTree = vierApi.getContentTree(newTokens.accessToken())
}

package be.tapped.vier

fun main(args: Array<String>) {
    val userName = args[0]
    val password = args[1]
    val tokenProvider = VierTokenProvider()
    tokenProvider.login(userName, password)
}

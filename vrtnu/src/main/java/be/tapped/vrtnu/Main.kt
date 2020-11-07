package be.tapped.vrtnu

fun main(args: Array<String>) {
    val userName = args[0]
    val password = args[1]
    println(TokenResolver.login(userName, password))
}

package be.tapped.goplay

internal object CredentialsProvider {
    private const val USERNAME_SYSTEM_ENV_KEY = "goplay.username"
    private const val PASSWORD_SYSTEM_ENV_KEY = "goplay.password"

    val credentials: TestCredentials by lazy {
        val username = requireNotNull(System.getProperty(USERNAME_SYSTEM_ENV_KEY)) {
            "No GoPlay username found. Be sure to add it to the System environments. $USERNAME_SYSTEM_ENV_KEY"
        }
        val password = requireNotNull(System.getProperty(PASSWORD_SYSTEM_ENV_KEY)) {
            "No GoPlay password found. Be sure to add it to the System environments. $PASSWORD_SYSTEM_ENV_KEY"
        }

        TestCredentials(username, password)
    }
}

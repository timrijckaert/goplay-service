package be.tapped.vier

internal object CredentialsProvider {
    private const val USERNAME_SYSTEM_ENV_KEY = "vier.username"
    private const val PASSWORD_SYSTEM_ENV_KEY = "vier.password"

    val credentials: TestCredentials by lazy {
        val username = System.getProperty(USERNAME_SYSTEM_ENV_KEY) ?: System.getenv(USERNAME_SYSTEM_ENV_KEY)
        val password = System.getProperty(PASSWORD_SYSTEM_ENV_KEY) ?: System.getenv(PASSWORD_SYSTEM_ENV_KEY)

        checkNotNull(username) {
            "No VRT username found. Be sure to add it to the System environments. $USERNAME_SYSTEM_ENV_KEY"
        }

        checkNotNull(password) {
            "No VRT password found. Be sure to add it to the System environments. $PASSWORD_SYSTEM_ENV_KEY"
        }

        TestCredentials(username, password)
    }
}

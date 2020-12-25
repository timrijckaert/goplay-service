package be.tapped.vtmgo

internal object CredentialsProvider {
    private const val USERNAME_SYSTEM_ENV_KEY = "vtmgo.username"
    private const val PASSWORD_SYSTEM_ENV_KEY = "vtmgo.password"

    val credentials: TestCredentials by lazy {
        val username = System.getProperty(USERNAME_SYSTEM_ENV_KEY) ?: System.getenv(USERNAME_SYSTEM_ENV_KEY)
        val password = System.getProperty(PASSWORD_SYSTEM_ENV_KEY) ?: System.getenv(PASSWORD_SYSTEM_ENV_KEY)

        checkNotNull(username) {
            "No VTM username found. Be sure to add it to the System environments. $USERNAME_SYSTEM_ENV_KEY"
        }

        checkNotNull(password) {
            "No VTM password found. Be sure to add it to the System environments. $PASSWORD_SYSTEM_ENV_KEY"
        }

        TestCredentials(username, password)
    }
}

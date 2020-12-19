package be.tapped.vrtnu

internal object CredentialsProvider {
    private const val USERNAME_SYSTEM_ENV_KEY = "vrtnu.username"
    private const val PASSWORD_SYSTEM_ENV_KEY = "vrtnu.password"

    val credentials: TestCredentials
        get() {
            val username = System.getProperty(USERNAME_SYSTEM_ENV_KEY)
            val password = System.getProperty(PASSWORD_SYSTEM_ENV_KEY)

            checkNotNull(username) {
                "No VRT username found. Be sure to add it to the System environments. $USERNAME_SYSTEM_ENV_KEY"
            }

            checkNotNull(password) {
                "No VRT password found. Be sure to add it to the System environments. $PASSWORD_SYSTEM_ENV_KEY"
            }

            return TestCredentials(username, password)
        }
}

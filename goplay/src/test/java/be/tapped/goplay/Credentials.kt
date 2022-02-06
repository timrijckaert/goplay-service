package be.tapped.goplay

internal data class Credentials(val username: String, val password: String) {
    companion object {
        private const val USERNAME_SYSTEM_ENV_KEY = "goplay.username"
        private const val PASSWORD_SYSTEM_ENV_KEY = "goplay.password"

        val default: Credentials by lazy {
            val username = requireNotNull(System.getenv(USERNAME_SYSTEM_ENV_KEY)) {
                "No GoPlay username found. Be sure to add it to the System environments. $USERNAME_SYSTEM_ENV_KEY"
            }
            val password = requireNotNull(System.getenv(PASSWORD_SYSTEM_ENV_KEY)) {
                "No GoPlay password found. Be sure to add it to the System environments. $PASSWORD_SYSTEM_ENV_KEY"
            }

            Credentials(username, password)
        }
    }
}


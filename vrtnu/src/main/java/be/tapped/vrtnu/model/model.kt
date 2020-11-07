package be.tapped.vrtnu.model

sealed class VRTLogin {
    sealed class Success : VRTLogin() {
        data class OK(val token: VRTToken) : Success()
    }

    sealed class Failure : VRTLogin() {
        data class Exceptionally(val e: Throwable) : Failure()
        object InvalidCredentials : Failure()
        object MissingLoginId : Failure()
        object MissingPassword : Failure()
        object Unknown : Failure()
    }
}

data class VRTToken(val cookieName: String, val expirationDate: String)

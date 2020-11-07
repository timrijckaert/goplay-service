package be.tapped.vrtnu.model

sealed class VRTLogin {
    sealed class Success : VRTLogin() {
        data class OK(val token: Token) : Success()
    }

    sealed class Failure : VRTLogin() {
        data class Exceptionally(val e: Throwable) : Failure()
        object InvalidCredentials : Failure()
        object MissingLoginId : Failure()
        object MissingPassword : Failure()
        object Unknown : Failure()
    }
}

inline class VRTNUToken(val token: String)
inline class VRTProfileToken(val token: String)
inline class RefreshToken(val token: String)

data class Token(
    val vrtnuToken: VRTNUToken,
    val vrtProfileToken: VRTProfileToken,
    val refreshToken: RefreshToken,
    val expiry: Long
)

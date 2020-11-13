package be.tapped.vrtnu.authentication

sealed class VRTLogin {
    sealed class Success(val tokenWrapper: TokenWrapper) : VRTLogin()
    sealed class Failure : VRTLogin() {
        data class Exceptionally(val e: Throwable) : Failure()
        object InvalidCredentials : Failure()
        object MissingLoginId : Failure()
        object MissingPassword : Failure()
        object Unknown : Failure()
    }
}

inline class OIDCXSRF(val token: String)

inline class State(val state: String)
inline class Session(val session: String)

inline class XVRTToken(val token: String)
inline class AccessToken(val token: String)
inline class RefreshToken(val token: String)
inline class Expiry(val date: Long)

data class TokenWrapper(
    val xVRTToken: XVRTToken,
    val accessToken: AccessToken,
    val refreshToken: RefreshToken,
    val expiry: Expiry,
)

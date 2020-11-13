package be.tapped.vrtnu.authentication

inline class OIDCXSRF(val token: String)

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

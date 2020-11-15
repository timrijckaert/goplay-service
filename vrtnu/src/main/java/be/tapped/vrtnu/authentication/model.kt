package be.tapped.vrtnu.authentication

inline class OIDCXSRF(val token: String)

inline class XVRTToken(val token: String)
inline class AccessToken(val token: String)
inline class RefreshToken(val token: String)
inline class Expiry(val date: Long)

inline class VRTPlayerToken(val token: String)

data class TokenWrapper(
    val accessToken: AccessToken,
    val refreshToken: RefreshToken,
    val expiry: Expiry,
)

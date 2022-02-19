package be.tapped.goplay.profile

@JvmInline
public value class AccessToken(public val token: String)

@JvmInline
public value class RefreshToken(public val token: String)

@JvmInline
public value class IdToken(public val token: String)

@JvmInline
public value class Expiry(public val dateInMillis: Long)

public data class TokenWrapper(
    val accessToken: AccessToken,
    val expiry: Expiry,
    val tokenType: String,
    val refreshToken: RefreshToken,
    val idToken: IdToken,
)

public data class Profile(
    val username: String,
    val sub: String? = null,
    val birthDate: String? = null,
    val gender: String? = null,
    val postalCode: String? = null,
    val selligentId: String? = null,
    val name: String? = null,
    val familyName: String? = null,
    val email: String? = null,
)

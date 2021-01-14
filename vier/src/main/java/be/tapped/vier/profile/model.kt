package be.tapped.vier.profile

public inline class AccessToken(public val token: String)
public inline class RefreshToken(public val token: String)
public inline class IdToken(public val token: String)
public inline class Expiry(public val dateInMillis: Long)

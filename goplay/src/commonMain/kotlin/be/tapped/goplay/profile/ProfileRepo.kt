package be.tapped.goplay.profile

import arrow.core.Either
import be.tapped.goplay.Authentication
import be.tapped.goplay.Failure

internal interface ProfileRepo {
    suspend fun fetchTokens(username: String, password: String): Either<Failure, Authentication.Token>
    suspend fun refreshTokens(refreshToken: RefreshToken): Either<Failure, Authentication.Token>
    suspend fun getUserAttributes(accessToken: AccessToken): Either<Failure, Authentication.Profile>
}

internal expect val httpProfileRepo: ProfileRepo

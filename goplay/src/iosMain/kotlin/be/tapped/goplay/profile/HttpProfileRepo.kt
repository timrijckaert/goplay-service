package be.tapped.goplay.profile

import arrow.core.Either
import be.tapped.goplay.Authentication
import be.tapped.goplay.Failure

internal actual val httpProfileRepo: ProfileRepo get() = HttpProfileRepo()

class HttpProfileRepo : ProfileRepo {
    override suspend fun fetchTokens(username: String, password: String): Either<Failure, Authentication.Token> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshTokens(refreshToken: RefreshToken): Either<Failure, Authentication.Token> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserAttributes(accessToken: AccessToken): Either<Failure, Authentication.Profile> {
        TODO("Not yet implemented")

    }
}

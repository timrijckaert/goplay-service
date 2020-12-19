package be.tapped.vrtnu.profile

import be.tapped.vrtnu.CredentialsProvider
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.StringSpec

public class TokenRepoTest : StringSpec({
    val tokenRepo = ProfileRepo()
    val (username, password) = CredentialsProvider.credentials

    "fetch token wrapper" {
        val tokenWrapper = tokenRepo.fetchTokenWrapper(username, password)
        tokenWrapper.shouldBeRight()
    }

    "refresh token wrapper" {
        val tokenWrapper = tokenRepo.fetchTokenWrapper(username, password).orNull()!!
        val newTokenWrapper = tokenRepo.refreshTokenWrapper(tokenWrapper.tokenWrapper.refreshToken)
        newTokenWrapper.shouldBeRight()
    }

    "fetch XVRT Token" {
        tokenRepo.fetchXVRTToken(username, password).shouldBeRight()
    }

    "fetch VRT Player Token" {
        tokenRepo.fetchVRTPlayerToken(tokenRepo.fetchXVRTToken(username, password).orNull()!!.xVRTToken).shouldBeRight()
    }
})

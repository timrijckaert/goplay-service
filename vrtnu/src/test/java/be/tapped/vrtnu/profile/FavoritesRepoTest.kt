package be.tapped.vrtnu.profile

import be.tapped.vrtnu.CredentialsProvider
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.StringSpec

public class FavoritesRepoTest : StringSpec({
    val favoritesRepo = ProfileRepo()
    val (username, password) = CredentialsProvider.credentials

    "favorites" {
        val xVRTToken = favoritesRepo.fetchXVRTToken(username, password).orNull()!!.xVRTToken
        favoritesRepo.favorites(xVRTToken).shouldBeRight()
    }
})

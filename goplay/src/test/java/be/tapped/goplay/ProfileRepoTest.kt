package be.tapped.goplay

import be.tapped.goplay.profile.HttpProfileRepo
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FreeSpec

internal class ProfileRepoTest : FreeSpec({
    "with valid credentials" - {
        val (username, password) = Credentials.default
        val sut = HttpProfileRepo()

        "should be able to fetch tokens" {
            val token = sut.fetchTokens(username, password).shouldBeRight().token

            "with those tokens" - {
                "it should be able to refresh the tokens" {
                    sut.refreshTokens(token.refreshToken).shouldBeRight()
                }

                "it should be able to get the user attributes" {
                    sut.getUserAttributes(token.accessToken).shouldBeRight()
                }
            }
        }
    }

    "with non valid credentials" - {
        val sut = HttpProfileRepo()

        "it should not be able to fetch tokens" {
            sut.fetchTokens("john-doe", "password").shouldBeLeft()
        }
    }
})

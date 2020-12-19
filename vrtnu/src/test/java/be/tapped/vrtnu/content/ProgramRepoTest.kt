package be.tapped.vrtnu.content

import be.tapped.vrtnu.ApiResponse
import be.tapped.vrtnu.common.defaultOkHttpClient
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string

public class ProgramRepoTest : StringSpec({
    val programRepo = HttpProgramRepo(defaultOkHttpClient, JsonProgramParser())

    "fetching A-Z Programs" {
        val azPrograms = programRepo.fetchAZPrograms()
        azPrograms.shouldBeRight()
        azPrograms.orNull()!!.programs.shouldNotBeEmpty()
    }

    "fetching an existing Program" {
        val news = programRepo.fetchProgramByName("Het Journaal")
        news.shouldBeRight()
        news.orNull()!! shouldNotBe null
    }

    "fetching an non existing Program" {
        val nonExistingProgram = programRepo.fetchProgramByName(Arb.string().samples().first().value)
        nonExistingProgram.shouldBeRight()
        nonExistingProgram.orNull()!! shouldBe ApiResponse.Success.Content.SingleProgram(null)
    }
})

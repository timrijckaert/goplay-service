package be.tapped.vrtnu.content

import arrow.core.Either.Right
import be.tapped.vrtnu.ApiResponse
import be.tapped.vrtnu.common.defaultOkHttpClient
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.beOfType
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string

public class ProgramRepoTest : StringSpec({
    val programRepo = HttpProgramRepo(defaultOkHttpClient, JsonProgramParser())

    "fetching A-Z Programs" {
        val azPrograms = programRepo.fetchAZPrograms()
        azPrograms should beOfType<Right<ApiResponse.Success.Content.Programs>>()
        azPrograms.orNull()!!.programs.shouldNotBeEmpty()
    }

    "fetching an existing Program" {
        val news = programRepo.fetchProgramByName("Het Journaal")
        news should beOfType<Right<ApiResponse.Success.Content.SingleProgram>>()
        news.orNull()!! shouldNotBe null
    }

    "fetching an non existing Program" {
        val nonExistingProgram = programRepo.fetchProgramByName(Arb.string().samples().first().value)
        nonExistingProgram should beOfType<Right<ApiResponse.Success.Content.SingleProgram>>()
        nonExistingProgram.orNull()!! shouldBe ApiResponse.Success.Content.SingleProgram(null)
    }
})

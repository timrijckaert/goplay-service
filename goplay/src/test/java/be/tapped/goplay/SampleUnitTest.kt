package be.tapped.goplay

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

internal class SampleUnitTest : StringSpec({

    "sample unit test" {
        1 + 1 shouldBe 2
    }
})

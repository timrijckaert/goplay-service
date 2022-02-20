package be.tapped.goplay

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

internal class Test : StringSpec({
    "run on all targets" {
        true shouldBe false
    }
})

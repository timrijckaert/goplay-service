package be.tapped.goplay

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

internal class Test : ShouldSpec({
    should("run on all targets") {
        true shouldBe false
    }
})

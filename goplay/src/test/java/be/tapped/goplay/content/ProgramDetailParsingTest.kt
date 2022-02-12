package be.tapped.goplay.content

import be.tapped.goplay.jsonSerializer
import be.tapped.goplay.readFromResources
import be.tapped.goplay.safeDecodeFromString
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.ShouldSpec

internal class ProgramDetailParsingTest : ShouldSpec({
    should("be able to parse a program detail") {
        val programDetailJson = readFromResources("culinaire-speurneuzen.json")
        val programDetail = jsonSerializer.safeDecodeFromString<ProgramDetail>(programDetailJson)
        programDetail.shouldBeRight()
    }
})

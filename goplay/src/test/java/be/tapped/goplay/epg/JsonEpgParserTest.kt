package be.tapped.goplay.epg

import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.StringSpec

public class JsonEpgParserTest : StringSpec({
    "should be able to parse" {
        val epgJson = javaClass.classLoader?.getResourceAsStream("epg.json")!!.reader().readText()
        val epg = JsonEpgParser().parse(epgJson)
        epg.shouldBeRight()
    }
})

package be.tapped.vrtnu.content

import io.kotest.core.spec.style.StringSpec
import io.kotest.assertions.arrow.either.shouldBeRight

public class JsonStreamInformationParserTest : StringSpec({
    "should be able to parse the live stream " {
        val streamJson = javaClass.classLoader?.getResourceAsStream("live-stream.json")!!.reader().readText()
        val streamInformation = JsonStreamInformationParser().parse(streamJson)
        streamInformation.shouldBeRight()
    }

    "should be able to parse a vod stream" {
        val streamJson = javaClass.classLoader?.getResourceAsStream("vod-stream.json")!!.reader().readText()
        val streamInformation = JsonStreamInformationParser().parse(streamJson)
        streamInformation.shouldBeRight()
    }
})

package be.tapped.vtmgo.content

import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.StringSpec

public class JsonStreamResponseParserTest : StringSpec() {
    init {
        "should be able to parse live stream response" {
            val liveStreamJson = javaClass.classLoader?.getResourceAsStream("live-stream.json")!!.reader().readText()
            val streamResponse = JsonStreamResponseParser().parse(liveStreamJson)
            streamResponse.shouldBeRight()
        }

        "should be able to parse vod stream response" {
            val vodStreamJson = javaClass.classLoader?.getResourceAsStream("vod-stream.json")!!.reader().readText()
            val streamResponse = JsonStreamResponseParser().parse(vodStreamJson)
            streamResponse.shouldBeRight()
        }

        "should be able to parse JSON with markers" {
            val jsonWithMarkers = javaClass.classLoader?.getResourceAsStream("stream-response-with-markers.json")!!.reader().readText()
            val streamResponse = JsonStreamResponseParser().parse(jsonWithMarkers)
            streamResponse.shouldBeRight()
        }
    }
}

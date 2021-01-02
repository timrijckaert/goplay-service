package be.tapped.vtmgo.content

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe

public class JsonStreamResponseParserTest : StringSpec() {
    init {
        "should be able to parse live stream response" {
            val liveStreamJson = javaClass.classLoader?.getResourceAsStream("live-stream.json")!!.reader().readText()
            val streamResponse = JsonStreamResponseParser().parse(liveStreamJson)
            streamResponse.orNull() shouldNotBe null
        }

        "should be able to parse vod stream response" {
            val vodStreamJson = javaClass.classLoader?.getResourceAsStream("vod-stream.json")!!.reader().readText()
            val streamResponse = JsonStreamResponseParser().parse(vodStreamJson)
            streamResponse.orNull() shouldNotBe null
        }
    }
}

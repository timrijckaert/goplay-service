package be.tapped.vtmgo.content

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe

public class JsonLiveStreamResponseParserTest : StringSpec() {
    init {
        "should be able to parse" {
            val liveStreamJson = javaClass.classLoader?.getResourceAsStream("live-stream.json")!!.reader().readText()
            val streamResponse = JsonLiveStreamResponseParser().parse(liveStreamJson)
            streamResponse.orNull() shouldNotBe null
        }
    }
}

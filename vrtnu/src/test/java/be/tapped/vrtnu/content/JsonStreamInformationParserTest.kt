package be.tapped.vrtnu.content

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

public class JsonStreamInformationParserTest : StringSpec({
    "should be able to parse the live stream " {
        val streamJson = javaClass.classLoader?.getResourceAsStream("live-stream.json")!!.reader().readText()
        val streamInformation = JsonStreamInformationParser().parse(streamJson).orNull()!!
        streamInformation shouldBe StreamInformation(
            duration = 0,
            skinType = "live",
            title = "Eén LIVE",
            shortDescription = null,
            drm = "drmString",
            drmExpired = "2020-12-20T18:13:07.840Z",
            aspectRatio = null,
            targetUrls = listOf(
                TargetUrl(type = TargetUrlType.MPEG_DASH, url = "mpeg_dash_url"),
                TargetUrl(type = TargetUrlType.HLS, url = "hls_url"),
                TargetUrl(type = TargetUrlType.HLS_AES, url = "hls_aes_url")
            ),
            posterImageUrl = null,
            channelId = "vualto_een_geo",
            playlist = PlayList(content = emptyList()),
            chaptering = Chaptering(content = emptyList())
        )
    }

    "should be able to parse a vod stream" {
        val streamJson = javaClass.classLoader?.getResourceAsStream("vod-stream.json")!!.reader().readText()
        val streamInformation = JsonStreamInformationParser().parse(streamJson).orNull()!!
        streamInformation shouldBe StreamInformation(
            duration = 1378050,
            skinType = "vod",
            title = "Trollen kun je niet opsluiten",
            shortDescription = "Trollen kun je niet opsluiten",
            drm = null,
            drmExpired = null,
            aspectRatio = "16:9",
            targetUrls = listOf(
                TargetUrl(type = TargetUrlType.HLS, url = "hls_url"),
                TargetUrl(type = TargetUrlType.MPEG_DASH, url = "mpeg_dash_url")
            ),
            posterImageUrl = "https://images.vrt.be/orig/2020/12/19/bdd8cad3-41a6-11eb-aae0-02b7b76bf47f.jpg",
            channelId = null,
            playlist = PlayList(content = emptyList()),
            chaptering = Chaptering(content = emptyList())
        )
    }
})

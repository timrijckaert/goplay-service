package be.tapped.vtmgo.content

import be.tapped.common.executeAsync
import be.tapped.vtmgo.common.HeaderBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.intellij.lang.annotations.Language

interface StreamRepo {
    suspend fun fetchStream(liveChannel: LiveChannel)
}

internal class HttpStreamRepo(
    private val client: OkHttpClient,
    private val headerBuilder: HeaderBuilder,
) : StreamRepo {
    override suspend fun fetchStream(liveChannel: LiveChannel) {
        withContext(Dispatchers.IO) {
            val liveStreamResponse = client.executeAsync(
                Request.Builder()
                    .get()
                    .headers(constructHeaders())
                    .url(constructUrl(liveChannel.channelId))
                    .build()
            )

            @Language("JSON") val jsonResponse = """
                {
                  "video": {
                    "streamType": "live",
                    "streams": [
                      {
                        "type": "anvato",
                        "anvato": {
                          "video": "vtm",
                          "mcp": "ONEMCP1",
                          "accessKey": "",
                          "token": ""
                        }
                      }
                    ],
                    "analytics": {
                      "cim": {
                        "identifier": "zUBFza8qGU9r1Oril70XvsV3.h3NNobQxe79BTAvPDb.P7",
                        "materialId": "d8659669-b964-414c-aa9c-e31d8d15696b",
                        "sourceType": "vid.tvi.live.free",
                        "contentType": "ce/tv",
                        "name": "Rip 2020",
                        "linkTv": "259136777449527",
                        "channel": "VTM"
                      }
                    },
                    "ads": {
                      "provider": "freewheel",
                      "freewheel": {
                        "serverSide": true,
                        "serverUrl": "https://5e124.v.fwmrm.net/ad/g/1",
                        "profileId": "385316:medialaan_VTMGO_SSAI_android",
                        "networkId": "385316"
                      }
                    },
                    "metadata": {
                      "id": "ab26ed57-bbb9-4df8-8331-57ad5e311162",
                      "geoBlocked": false,
                      "availability": 28888,
                      "assetType": "broadcast",
                      "title": "RIP 2020",
                      "channel": {
                        "id": "d8659669-b964-414c-aa9c-e31d8d15696b",
                        "title": "VTM"
                      },
                      "videoType": "episode",
                      "program": {
                        "id": "41f18088-4216-4471-a939-f91665c5380f",
                        "title": "Rip 2020"
                      },
                      "broadcast": {
                        "id": "0ae6a593-9a7b-4847-9302-22ac8a21cd51",
                        "technicalFromMs": 1606669884680,
                        "technicalToMs": 1606672785000
                      },
                      "synopsis": {
                        "xs": "Het nieuws van het voorbije jaar? Dat kon een pak vrolijker. Daarom spoelen Nathalie, Jonas, Guga en Ruth 2020 helemaal terug en geven het een make-over waar je wel happy van wordt.",
                        "s": "Het nieuws van het voorbije jaar? Dat kon een pak vrolijker. Daarom spoelen Nathalie, Jonas, Guga en Ruth 2020 helemaal terug en geven het een make-over waar je wel happy van wordt. Locatie van deze pop-up comedyshow is de stadsschouwburg van Mechelen. Welke plek is immers een beter symbool voor dit jaar dan een lege theaterzaal?",
                        "m": "Het nieuws van het voorbije jaar? Dat kon een pak vrolijker. Daarom spoelen Nathalie, Jonas, Guga en Ruth 2020 helemaal terug en geven het een make-over waar je wel happy van wordt. Locatie van deze pop-up comedyshow is de stadsschouwburg van Mechelen. Welke plek is immers een beter symbool voor dit jaar dan een lege theaterzaal?"
                      },
                      "episode": {
                        "order": 1,
                        "season": {
                          "order": 1
                        }
                      },
                      "legalTags": [
                        "PG_ALL"
                      ],
                      "posterImages": [
                        {
                          "height": 480,
                          "url": "https://images0.persgroep.net/rcs/0-O9rB4aPl4AZFa-D_Om99TAQW8/diocontent/179480238/_fitheight/480?appId=9cd8f8ad85847bdd1f55f72558a588b5"
                        },
                        {
                          "height": 720,
                          "url": "https://images1.persgroep.net/rcs/Q9OVPuZBsmyMetL8GwsLcmXp1GI/diocontent/179480238/_fitheight/720?appId=9cd8f8ad85847bdd1f55f72558a588b5"
                        },
                        {
                          "height": 1080,
                          "url": "https://images1.persgroep.net/rcs/XirtZZkv0EAuWxTVqNOQg8qnWEY/diocontent/179480238/_fitheight/1080?appId=9cd8f8ad85847bdd1f55f72558a588b5"
                        },
                        {
                          "height": 2160,
                          "url": "https://images2.persgroep.net/rcs/76HSlxxY6swnxn2QbvgRT52FbyE/diocontent/179480238/_fitheight/2160?appId=9cd8f8ad85847bdd1f55f72558a588b5"
                        }
                      ]
                    }
                  }
                }
            """.trimIndent()
        }
    }

    companion object {
        private const val POPCORN_API_KEY = "zTxhgTEtb055Ihgw3tN158DZ0wbbaVO86lJJulMl"
    }

    private fun constructHeaders(): Headers =
        Headers.Builder()
            .addAll(headerBuilder.defaultHeaders)
            .add("x-api-key", POPCORN_API_KEY)
            .add("Popcorn-SDK-Version", "2")
            //TODO check if this is needed since it returns the same response for the channels
            .add("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 6.0.1; Nexus 5 Build/M4B30Z)")
            .build()

    private fun constructUrl(channelId: String): HttpUrl =
        HttpUrl.Builder()
            .scheme("https")
            .host("videoplayer-service.api.persgroep.cloud")
            .addPathSegment("config")
            //TODO should be generic to function for all types
            .addPathSegment("channels")
            .addQueryParameter("startPosition", "0.0")
            .addQueryParameter("autoPlay", "true")
            .addPathSegment(channelId)
            .build()

}

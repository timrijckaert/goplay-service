package be.tapped.vtmgo.content

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.handleErrorWith
import arrow.core.right
import be.tapped.common.executeAsync
import be.tapped.vtmgo.ApiResponse
import be.tapped.vtmgo.common.safeBodyString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.Headers.Companion.toHeaders
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

internal class AnvatoVideoJsonLoadedParser {

    private val jsJsonExtractionRegex = Regex("anvatoVideoJSONLoaded\\((.*)\\)")

    suspend fun getFirstPublishedUrl(jsFunction: String): Either<ApiResponse.Failure, PublishedUrl> =
        either {
            val json = !Either.fromNullable(jsJsonExtractionRegex.findAll(jsFunction).toList().firstOrNull()?.groups?.get(1)?.value)
                .mapLeft { ApiResponse.Failure.Stream.NoAnvatoResponseFound }
            val anvatoVideoStreamResponse = !Either.catch { Json.decodeFromString<AnvatoVideoStreamResponse>(json) }
                .mapLeft(ApiResponse.Failure::JsonParsingException)
            !Either.fromNullable(anvatoVideoStreamResponse.publishedUrls.firstOrNull())
                .mapLeft { ApiResponse.Failure.Stream.NoPublishedEmbedUrlFound }
        }
}

interface AnvatoRepo {
    suspend fun fetchStream(anvato: Anvato, streamResponse: StreamResponse): Either<ApiResponse.Failure, AnvatoStreamWrapper>
}

internal class HttpAnvatoResponse(
    private val client: OkHttpClient,
    private val anvatoVideoJsonLoadedParser: AnvatoVideoJsonLoadedParser,
) : AnvatoRepo {

    companion object {
        private const val ANVATO_USER_AGENT = "ANVSDK Android/5.0.39 (Linux; Android 6.0.1; Nexus 5)"
    }

    override suspend fun fetchStream(anvato: Anvato, streamResponse: StreamResponse): Either<ApiResponse.Failure, AnvatoStreamWrapper> =
        withContext(Dispatchers.IO) {
            val anvatoRequestJson = constructAnvatoRequestBody(streamResponse.ads.freewheel, anvato)

            val anvatoUrl = HttpUrl
                .Builder()
                .scheme("https")
                .host("tkx.apis.anvato.net")
                .addPathSegments("rest/v2/mcp/video")
                .addPathSegment(anvato.video)
                .addQueryParameter("rtyp", "fp")
                .addQueryParameter("anvack", anvato.accessKey)
                .addQueryParameter("anvtrid", getRandomString(32))
                .build()

            val anvatoResponse = client.executeAsync(
                Request.Builder()
                    .headers(
                        mapOf(
                            "X-Anvato-User-Agent" to ANVATO_USER_AGENT,
                            "User-Agent" to ANVATO_USER_AGENT,
                        ).toHeaders()
                    )
                    .post(anvatoRequestJson.toRequestBody())
                    .url(anvatoUrl)
                    .build()
            )

            either {
                val firstPublishedUrl = !anvatoVideoJsonLoadedParser.getFirstPublishedUrl(!anvatoResponse.safeBodyString())

                val mpdManifestUrl = !mpdManifestUrl(firstPublishedUrl.embedUrl)
                val backUpMpdManifestUrl = !mpdManifestUrl(firstPublishedUrl.backupUrl)
                val licenseUrl = firstPublishedUrl.licenseUrl
                val backUpLicenseUrl = firstPublishedUrl.backupLicenseUrl

                AnvatoStreamWrapper(
                    rawMdpUrl = MPDUrl(firstPublishedUrl.embedUrl),
                    mdpUrl = MPDUrl(mpdManifestUrl),
                    rawBackUpMdpUrl = MPDUrl(firstPublishedUrl.backupUrl),
                    backUpMdpUrl = MPDUrl(backUpMpdManifestUrl),
                    licenseUrl = LicenseUrl(licenseUrl),
                    backUpLicenseUrl = LicenseUrl(backUpLicenseUrl)
                )
            }
        }

    private suspend fun mpdManifestUrl(publishedUrl: String): Either<ApiResponse.Failure, String> {
        val redirectLocationXMLRegex = Regex("<Location>([^<]+)</Location>")
        suspend fun downloadRaw(url: String): Either<ApiResponse.Failure, String> =
            withContext(Dispatchers.IO) {
                val response = client.executeAsync(
                    Request.Builder()
                        .get()
                        .url(url)
                        .headers(mapOf(
                            "X-Anvato-User-Agent" to ANVATO_USER_AGENT,
                            "User-Agent" to ANVATO_USER_AGENT,
                        ).toHeaders())
                        .build()
                )
                response.safeBodyString()
            }

        return either {
            val xml = !downloadRaw(publishedUrl)
            !Either
                .fromNullable(
                    redirectLocationXMLRegex
                        .findAll(xml)
                        .toList()
                        .firstOrNull()
                        ?.groups?.get(1)
                        ?.value
                ).handleErrorWith { publishedUrl.right() }
                .mapLeft { ApiResponse.Failure.Stream.NoMPDManifestUrlFound }
        }
    }

    private fun getRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private fun constructAnvatoRequestBody(freeWheel: Freewheel, anvato: Anvato): String =
        //language=JSON
        """
{
  "ads": {
    "freewheel": {
      "custom": {
        "ml_userid": "",
        "ml_dmp_userid": "",
        "ml_gdprconsent": "",
        "ml_apple_advertising_id": "",
        "ml_google_advertising_id": ""
      },
      "network_id": "${freeWheel.networkId}",
      "profile_id": "${freeWheel.profileId}",
      "server_url": "${freeWheel.serverUrl}",
      "site_section_id": "mdl_vtmgo_phone_android_default",
      "video_asset_id": "${freeWheel.assetId ?: ""}"
    }
  },
  "api": {
    "anvstk2": "${anvato.token}"
  },
  "content": {
    "mcp_video_id": "${anvato.video}"
  },
  "sdkver": "5.0.39",
  "user": {
    "adobepass": {
      "err_msg": "",
      "maxrating": "",
      "mvpd": "",
      "resource": "",
      "short_token": ""
    },
    "device": "android",
    "device_id": ""
  },
  "version": "3.0"
}
"""
}

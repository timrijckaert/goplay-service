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
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.Headers.Companion.toHeaders
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

internal class AnvatoJsonJavascriptFunctionExtractor {
    private val jsJsonExtractionRegex = Regex("anvatoVideoJSONLoaded\\((.*)\\)")

    fun getJSONFromJavascript(jsFunction: String): Either<ApiResponse.Failure.Stream.NoJSONFoundInAnvatoJavascriptFunction, String> =
        Either
            .fromNullable(jsJsonExtractionRegex.findAll(jsFunction).toList().firstOrNull()?.groups?.get(1)?.value)
            .mapLeft { ApiResponse.Failure.Stream.NoJSONFoundInAnvatoJavascriptFunction }
}

internal class AnvatoPublishedUrlParser {
    suspend fun parse(anvatoPublishedUrl: JsonObject): Either<ApiResponse.Failure, AnvatoPublishedUrl> =
        Either.catch {
            AnvatoPublishedUrl(
                anvatoPublishedUrl["embed_url"]!!.jsonPrimitive.content,
                anvatoPublishedUrl["license_url"]!!.jsonPrimitive.content,
                anvatoPublishedUrl["backup_url"]?.jsonPrimitive?.content,
                anvatoPublishedUrl["backup_license_url"]?.jsonPrimitive?.content,
            )
        }.mapLeft(ApiResponse.Failure::JsonParsingException)
}

internal class AnvatoVideoJsonParser(
    private val anvatoJsonJavascriptFunctionExtractor: AnvatoJsonJavascriptFunctionExtractor,
    private val anvatoPublishedUrlParser: AnvatoPublishedUrlParser,
) {

    suspend fun getFirstPublishedUrl(jsFunction: String): Either<ApiResponse.Failure, AnvatoPublishedUrl> =
        either {
            val json = !anvatoJsonJavascriptFunctionExtractor.getJSONFromJavascript(jsFunction)
            val firstPublishedJsonObj = !Either.fromNullable(
                !Either.catch {
                    Json.decodeFromString<JsonObject>(json)["published_urls"]?.jsonArray?.get(0)?.jsonObject
                }.mapLeft(ApiResponse.Failure::JsonParsingException)
            ).mapLeft { ApiResponse.Failure.Stream.NoPublishedEmbedUrlFound }
            !anvatoPublishedUrlParser.parse(firstPublishedJsonObj)
        }
}

interface AnvatoRepo {

    suspend fun fetchStream(anvato: Anvato, streamResponse: StreamResponse): Either<ApiResponse.Failure, AnvatoStreamWrapper>

}

internal class HttpAnvatoResponse(
    private val client: OkHttpClient,
    private val anvatoVideoJsonParser: AnvatoVideoJsonParser,
) : AnvatoRepo {

    companion object {
        private const val ANVATO_USER_AGENT = "ANVSDK Android/5.0.39 (Linux; Android 6.0.1; Nexus 5)"
    }

    private val anvatoHeaders =
        mapOf(
            "X-Anvato-User-Agent" to ANVATO_USER_AGENT,
            "User-Agent" to ANVATO_USER_AGENT,
        ).toHeaders()

    override suspend fun fetchStream(anvato: Anvato, streamResponse: StreamResponse): Either<ApiResponse.Failure, AnvatoStreamWrapper> =
        withContext(Dispatchers.IO) {
            val anvatoResponse = client.executeAsync(
                Request.Builder()
                    .headers(anvatoHeaders)
                    .post(constructAnvatoRequestBody(streamResponse.ads.freewheel, anvato))
                    .url(HttpUrl
                        .Builder()
                        .scheme("https")
                        .host("tkx.apis.anvato.net")
                        .addPathSegments("rest/v2/mcp/video")
                        .addPathSegment(anvato.video)
                        .addQueryParameter("rtyp", "fp")
                        .addQueryParameter("anvack", anvato.accessKey)
                        .addQueryParameter("anvtrid", getRandomString(32))
                        .build())
                    .build()
            )

            either {
                val firstPublishedUrl = !anvatoVideoJsonParser.getFirstPublishedUrl(!anvatoResponse.safeBodyString())

                val mpdManifestUrl = !mpdManifestUrl(firstPublishedUrl.embedUrl)
                val backUpMpdManifestUrl = firstPublishedUrl.backupUrl?.let { !mpdManifestUrl(it) }?.let(::MPDUrl)
                val licenseUrl = firstPublishedUrl.licenseUrl
                val backUpLicenseUrl = firstPublishedUrl.backupLicenseUrl

                AnvatoStreamWrapper(
                    rawMdpUrl = MPDUrl(firstPublishedUrl.embedUrl),
                    mdpUrl = MPDUrl(mpdManifestUrl),
                    rawBackUpMdpUrl = firstPublishedUrl.backupUrl?.let(::MPDUrl),
                    backUpMdpUrl = backUpMpdManifestUrl,
                    licenseUrl = LicenseUrl(licenseUrl),
                    backUpLicenseUrl = backUpLicenseUrl?.let(::LicenseUrl)
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
                        .headers(anvatoHeaders)
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

    private fun constructAnvatoRequestBody(freeWheel: Freewheel, anvato: Anvato): RequestBody =
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
""".toRequestBody()
}

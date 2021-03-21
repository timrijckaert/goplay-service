package be.tapped.vtmgo.content

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.flatMap
import arrow.core.handleErrorWith
import arrow.core.right
import be.tapped.common.internal.executeAsync
import be.tapped.vtmgo.ApiResponse
import be.tapped.vtmgo.common.safeBodyString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*
import okhttp3.*
import okhttp3.Headers.Companion.toHeaders
import okhttp3.RequestBody.Companion.toRequestBody

internal class AnvatoJsonJavascriptFunctionExtractor {
    private val jsJsonExtractionRegex = Regex("anvatoVideoJSONLoaded\\((.*)\\)")

    fun getJSONFromJavascript(jsFunction: String): Either<ApiResponse.Failure.Stream.NoJSONFoundInAnvatoJavascriptFunction, String> =
            Either.fromNullable(jsJsonExtractionRegex.findAll(jsFunction).toList().firstOrNull()?.groups?.get(1)?.value)
                    .mapLeft { ApiResponse.Failure.Stream.NoJSONFoundInAnvatoJavascriptFunction }
}

internal class AnvatoPublishedUrlParser {
    fun parse(anvatoPublishedUrl: JsonObject): Either<ApiResponse.Failure, AnvatoPublishedUrl> = Either.catch {
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

    fun getFirstPublishedUrl(jsFunction: String): Either<ApiResponse.Failure, AnvatoPublishedUrl> =
            anvatoJsonJavascriptFunctionExtractor.getJSONFromJavascript(jsFunction)
                    .flatMap {
                        Either.catch {
                            Json.decodeFromString<JsonObject>(it)["published_urls"]?.jsonArray?.get(0)?.jsonObject
                        }.mapLeft(ApiResponse.Failure::JsonParsingException)
                    }
                    .flatMap { Either.fromNullable(it).mapLeft { ApiResponse.Failure.Stream.NoPublishedEmbedUrlFound } }
                    .flatMap(anvatoPublishedUrlParser::parse)
}

internal class AnvatoMasterM3U8JsonParser {
    fun parse(json: String): Either<ApiResponse.Failure, M3U8Url> = Either.catch {
        M3U8Url(Json.decodeFromString<JsonObject>(json)["master_m3u8"]!!.jsonPrimitive.content)
    }.mapLeft(ApiResponse.Failure::JsonParsingException)
}

public sealed interface AnvatoRepo {

    public suspend fun fetchLiveStream(anvato: Anvato, streamResponse: StreamResponse): Either<ApiResponse.Failure, AnvatoStream.Live>

    public suspend fun fetchEpisodeStream(anvato: Anvato, streamResponse: StreamResponse): Either<ApiResponse.Failure, AnvatoStream.Episode>

}

internal class HttpAnvatoRepo(
        private val client: OkHttpClient,
        private val anvatoVideoJsonParser: AnvatoVideoJsonParser,
        private val anvatoMasterM3U8JsonParser: AnvatoMasterM3U8JsonParser,
) : AnvatoRepo {

    companion object {
        private const val ANVATO_USER_AGENT = "ANVSDK Android/5.0.39 (Linux; Android 6.0.1; Nexus 5)"
    }

    private val anvatoHeaders = mapOf(
            "X-Anvato-User-Agent" to ANVATO_USER_AGENT,
            "User-Agent" to ANVATO_USER_AGENT,
    ).toHeaders()

    override suspend fun fetchLiveStream(anvato: Anvato, streamResponse: StreamResponse): Either<ApiResponse.Failure, AnvatoStream.Live> =
            withContext(Dispatchers.IO) {
                val anvatoResponse = fetchPublishedUrlResponse(streamResponse, anvato)

                either {
                    val firstPublishedUrl = !anvatoVideoJsonParser.getFirstPublishedUrl(!anvatoResponse.safeBodyString())

                    val mpdManifestUrl = !mpdManifestUrl(firstPublishedUrl.embedUrl)
                    val backUpMpdManifestUrl = !mpdManifestUrl(firstPublishedUrl.backupUrl!!)
                    val licenseUrl = firstPublishedUrl.licenseUrl
                    val backUpLicenseUrl = firstPublishedUrl.backupLicenseUrl

                    AnvatoStream.Live(
                            rawMdpUrl = MPDUrl(firstPublishedUrl.embedUrl),
                            mdpUrl = MPDUrl(mpdManifestUrl),
                            rawBackUpMdpUrl = MPDUrl(firstPublishedUrl.backupUrl),
                            backUpMdpUrl = MPDUrl(backUpMpdManifestUrl),
                            licenseUrl = LicenseUrl(licenseUrl),
                            backUpLicenseUrl = LicenseUrl(backUpLicenseUrl!!)
                    )
                }
            }

    override suspend fun fetchEpisodeStream(anvato: Anvato, streamResponse: StreamResponse): Either<ApiResponse.Failure, AnvatoStream.Episode> =
            withContext(Dispatchers.IO) {
                val anvatoResponse = fetchPublishedUrlResponse(streamResponse, anvato)

                either {
                    val firstPublishedUrl = !anvatoVideoJsonParser.getFirstPublishedUrl(!anvatoResponse.safeBodyString())
                    val response = client.executeAsync(
                            Request.Builder().get().url(firstPublishedUrl.embedUrl).build()
                    )

                    AnvatoStream.Episode(
                            MPDUrl((!anvatoMasterM3U8JsonParser.parse(!response.safeBodyString())).url),
                            LicenseUrl(firstPublishedUrl.licenseUrl),
                            streamResponse.subtitles
                    )
                }
            }

    private suspend fun fetchPublishedUrlResponse(streamResponse: StreamResponse, anvato: Anvato): Response = client.executeAsync(
            Request.Builder().headers(anvatoHeaders).post(constructAnvatoRequestBody(streamResponse.ads.freewheel, anvato)).url(
                    HttpUrl.Builder().scheme("https").host("tkx.apis.anvato.net").addPathSegments("rest/v2/mcp/video").addPathSegment(anvato.video)
                            .addQueryParameter("rtyp", "fp").addQueryParameter("anvack", anvato.accessKey).addQueryParameter("anvtrid", getRandomString(32))
                            .build()
            ).build()
    )

    private suspend fun mpdManifestUrl(publishedUrl: String): Either<ApiResponse.Failure, String> {
        val redirectLocationXMLRegex = Regex("<Location>([^<]+)</Location>")
        suspend fun downloadRaw(url: String): Either<ApiResponse.Failure, String> = withContext(Dispatchers.IO) {
            val response = client.executeAsync(
                    Request.Builder().get().url(url).headers(anvatoHeaders).build()
            )
            response.safeBodyString()
        }

        return either {
            val xml = !downloadRaw(publishedUrl)
            !Either.fromNullable(
                    redirectLocationXMLRegex.findAll(xml).toList().firstOrNull()?.groups?.get(1)?.value
            ).handleErrorWith { publishedUrl.right() }.mapLeft { ApiResponse.Failure.Stream.NoMPDManifestUrlFound }
        }
    }

    private fun getRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length).map { allowedChars.random() }.joinToString("")
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

package be.tapped.vtmgo.profile

import be.tapped.common.DefaultCookieJar
import be.tapped.common.ReadOnlyCookieJar
import be.tapped.common.executeAsync
import com.moczul.ok2curl.CurlInterceptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

internal class JsonProfileParser {
    fun parse(json: String): List<Profile> = Json.decodeFromString(json)
}

class ProfileRepo(
    private val cookieJar: ReadOnlyCookieJar = CookieJar(DefaultCookieJar()),
    private val client: OkHttpClient =
        OkHttpClient.Builder()
            .addNetworkInterceptor(CurlInterceptor { message -> println("$message\n\r") })
            .cookieJar(cookieJar)
            .build(),
    jwtTokenFactory: JWTTokenFactory = VTMGOJWTTokenFactory(client, cookieJar),
) : JWTTokenFactory by jwtTokenFactory {

    private val headerBuilder: HeaderBuilder = HeaderBuilder()
    private val jsonProfileParser: JsonProfileParser = JsonProfileParser()

    companion object {
        private const val API_ENDPOINT = "https://lfvp-api.dpgmedia.net"
    }

    suspend fun getProfiles(jwtToken: JWT): List<Profile> =
        withContext(Dispatchers.IO) {
            val profiles = client.executeAsync(
                Request.Builder()
                    .get()
                    .headers(headerBuilder.authenticationHeaders(jwtToken))
                    .url("$API_ENDPOINT/profiles?products=VTM_GO,VTM_GO_KIDS")
                    .build()
            )

            jsonProfileParser.parse(profiles.body!!.string())
        }
}

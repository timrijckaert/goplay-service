package be.tapped.vtmgo.profile

import be.tapped.common.ReadOnlyCookieJar
import be.tapped.common.executeAsync
import be.tapped.vtmgo.common.AuthorizationHeaderBuilder
import be.tapped.vtmgo.common.HeaderBuilder
import be.tapped.vtmgo.common.defaultCookieJar
import be.tapped.vtmgo.common.defaultOkHttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

class ProfileRepo(
    private val cookieJar: ReadOnlyCookieJar = defaultCookieJar,
    private val client: OkHttpClient = defaultOkHttpClient,
    private val headerBuilder: HeaderBuilder = AuthorizationHeaderBuilder(),
    jwtTokenFactory: JWTTokenFactory = VTMGOJWTTokenFactory(client, cookieJar),
) : JWTTokenFactory by jwtTokenFactory {

    companion object {
        private const val API_ENDPOINT = "https://lfvp-api.dpgmedia.net"
    }

    //TODO Use Either to make it safe
    suspend fun getProfiles(jwtToken: JWT): List<Profile> =
        withContext(Dispatchers.IO) {
            val profiles = client.executeAsync(
                Request.Builder()
                    .get()
                    .headers(headerBuilder.authenticationHeaders(jwtToken))
                    .url("$API_ENDPOINT/profiles?products=VTM_GO,VTM_GO_KIDS")
                    .build()
            )

            Json.decodeFromString(profiles.body!!.string())
        }
}

package be.tapped.vtmgo.profile

import arrow.core.Either
import arrow.core.computations.either
import be.tapped.common.internal.executeAsync
import be.tapped.vtmgo.ApiResponse
import be.tapped.vtmgo.ApiResponse.Failure.JsonParsingException
import be.tapped.vtmgo.common.AuthorizationHeaderBuilder
import be.tapped.vtmgo.common.HeaderBuilder
import be.tapped.vtmgo.common.safeBodyString
import be.tapped.vtmgo.common.vtmApiDefaultOkHttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

public class JsonProfileParser {
    public fun parse(json: String): Either<ApiResponse.Failure, List<Profile>> =
            Either.catch { Json.decodeFromString<List<Profile>>(json) }.mapLeft { JsonParsingException(it, json) }
}

public sealed interface ProfileRepo {
    public suspend fun getProfiles(jwtToken: JWT): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.Profiles>
}

internal class HttpProfileRepo(
        private val client: OkHttpClient = vtmApiDefaultOkHttpClient,
        private val headerBuilder: HeaderBuilder = AuthorizationHeaderBuilder(),
        private val jsonProfileParser: JsonProfileParser = JsonProfileParser(),
) : ProfileRepo {

    private companion object {
        private const val API_ENDPOINT = "https://lfvp-api.dpgmedia.net"
    }

    // curl -X GET \
    // -H "x-app-version:8" \
    // -H "x-persgroep-mobile-app:true" \
    // -H "x-persgroep-os:android" \
    // -H "x-persgroep-os-version:25" \
    // -H "x-dpp-jwt: <jwt-token>" \
    // -H "Cookie:authId=<authId>" \
    // -H "User-Agent:okhttp/4.9.0" "https://lfvp-api.dpgmedia.net/profiles?products=VTM_GO,VTM_GO_KIDS"
    override suspend fun getProfiles(jwtToken: JWT): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.Profiles> =
            withContext(Dispatchers.IO) {
                val response = client.executeAsync(
                        Request.Builder()
                                .get()
                                .headers(headerBuilder.authenticationHeaders(jwtToken))
                                .url("$API_ENDPOINT/profiles?products=VTM_GO,VTM_GO_KIDS")
                                .build()
                )

                either {
                    val profiles = jsonProfileParser.parse(response.safeBodyString().bind()).bind()
                    ApiResponse.Success.Authentication.Profiles(profiles)
                }
            }
}

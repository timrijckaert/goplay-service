package be.tapped.vtmgo.content

import arrow.core.Either
import arrow.core.computations.either
import be.tapped.common.executeAsync
import be.tapped.common.validateResponse
import be.tapped.vtmgo.ApiResponse
import be.tapped.vtmgo.common.HeaderBuilder
import be.tapped.vtmgo.ApiResponse.Failure.JsonParsingException
import be.tapped.vtmgo.common.safeBodyString
import be.tapped.vtmgo.profile.JWT
import be.tapped.vtmgo.profile.Profile
import be.tapped.vtmgo.profile.VTMGOProduct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

internal class JsonPagedTeaserContentParser {
    suspend fun parse(json: String): Either<ApiResponse.Failure, List<PagedTeaserContent>> =
        Either.catch {
            val pagedTeasers = Json.decodeFromString<JsonObject>(json)["pagedTeasers"]!!.jsonObject["content"]!!
            Json.decodeFromJsonElement<List<PagedTeaserContent>>(pagedTeasers)
        }.mapLeft(::JsonParsingException)
}

interface ProgramRepo {
    suspend fun fetchAZPrograms(jwt: JWT, profile: Profile): Either<ApiResponse.Failure, ApiResponse.Success.Content.Programs>
}

internal class HttpProgramRepo(
    private val client: OkHttpClient,
    private val baseContentHttpUrlBuilder: BaseContentHttpUrlBuilder,
    private val headerBuilder: HeaderBuilder,
    private val jsonPagedTeaserContentParser: JsonPagedTeaserContentParser,
) : ProgramRepo {

    // curl -X GET \
    // -H "x-app-version:8" \
    // -H "x-persgroep-mobile-app:true" \
    // -H "x-persgroep-os:android" \
    // -H "x-persgroep-os-version:23" \
    // -H "x-dpp-jwt: <jwt-token>" \
    // -H "x-dpp-profile: <profile-id>" \
    // -H "Host:lfvp-api.dpgmedia.net" \
    // -H "Connection:Keep-Alive" \
    // -H "Accept-Encoding:gzip" \
    // -H "User-Agent:okhttp/4.9.0" "https://lfvp-api.dpgmedia.net/vtmgo/catalog?pageSize=2000"
    override suspend fun fetchAZPrograms(jwt: JWT, profile: Profile): Either<ApiResponse.Failure, ApiResponse.Success.Content.Programs> =
        withContext(Dispatchers.IO) {
            either {
                val response = client.executeAsync(
                    Request.Builder()
                        .headers(headerBuilder.authenticationHeaders(jwt, profile))
                        .get()
                        .url(constructUrl(profile.product))
                        .build()
                )

                ApiResponse.Success.Content.Programs(!jsonPagedTeaserContentParser.parse(!response.safeBodyString()))
            }
        }

    private fun constructUrl(vtmGoProduct: VTMGOProduct, filter: String? = null): HttpUrl =
        baseContentHttpUrlBuilder.constructBaseContentUrl(vtmGoProduct)
            .addPathSegments("catalog")
            .addQueryParameter("pageSize", "2000")
            .apply {
                filter?.let {
                    //Category Filter
                    addEncodedQueryParameter("filter", it)
                }
            }
            .build()
}

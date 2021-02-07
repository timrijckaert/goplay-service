package be.tapped.vtmgo.content

import arrow.core.Either
import arrow.core.computations.either
import be.tapped.common.internal.executeAsync
import be.tapped.vtmgo.ApiResponse
import be.tapped.vtmgo.ApiResponse.Failure.JsonParsingException
import be.tapped.vtmgo.common.HeaderBuilder
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
import kotlinx.serialization.json.jsonArray
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

public enum class StoreFrontType(internal val id: String) {
    MAIN("9620cc0b-0f97-4d96-902a-827dcfd0b227"),
    SERIES("1c683de4-3fb0-4cc4-9d9c-c365eba1b155"),
    MOVIES("e3fc0750-f110-4808-ae5f-246846ff940f"),
    KIDS("73f34fbf-301c-4deb-b366-13ba39e25996");
}

internal class JsonStoreFrontParser {
    private val jsonParser: Json = Json { classDiscriminator = "rowType" }

    suspend fun parseListOfStoreFront(json: String): Either<ApiResponse.Failure, List<StoreFront>> = Either.catch {
        val rows = jsonParser.decodeFromString<JsonObject>(json)["rows"]!!.jsonArray
        jsonParser.decodeFromJsonElement<List<StoreFront>>(rows)
    }.mapLeft(::JsonParsingException)
}

public sealed interface StoreFrontRepo {

    public suspend fun fetchStoreFront(
            jwt: JWT,
            profile: Profile,
            storeFrontType: StoreFrontType,
    ): Either<ApiResponse.Failure, ApiResponse.Success.Content.StoreFrontRows>

}

internal class HttpStoreFrontRepo(
    private val client: OkHttpClient,
    private val baseContentHttpUrlBuilder: BaseContentHttpUrlBuilder,
    private val headerBuilder: HeaderBuilder,
    private val jsonStoreFrontParser: JsonStoreFrontParser,
) : StoreFrontRepo {

    // curl -X GET \
    // -H "x-app-version:8" \
    // -H "x-persgroep-mobile-app:true" \
    // -H "x-persgroep-os:android" \
    // -H "x-persgroep-os-version:25" \
    // -H "x-dpp-jwt: <jwt-token>" \
    // -H "x-dpp-profile: <profile-id>" \
    // -H "Host:lfvp-api.dpgmedia.net" \
    // -H "Connection:Keep-Alive" \
    // -H "Accept-Encoding:gzip" \
    // -H "User-Agent:okhttp/4.9.0" "https://lfvp-api.dpgmedia.net/vtmgo/storefronts/<StoreFrontType.id>"
    override suspend fun fetchStoreFront(
        jwt: JWT,
        profile: Profile,
        storeFrontType: StoreFrontType,
    ): Either<ApiResponse.Failure, ApiResponse.Success.Content.StoreFrontRows> {
        fun constructUrl(product: VTMGOProduct, storeFrontType: StoreFrontType): HttpUrl =
            baseContentHttpUrlBuilder.constructBaseContentUrl(product).addPathSegment("storefronts").addPathSegment(storeFrontType.id).build()

        return withContext(Dispatchers.IO) {
            val response = client.executeAsync(
                Request.Builder().headers(headerBuilder.authenticationHeaders(jwt, profile)).get().url(constructUrl(profile.product, storeFrontType))
                    .build()
            )

            either {
                ApiResponse.Success.Content.StoreFrontRows(!jsonStoreFrontParser.parseListOfStoreFront(!response.safeBodyString()))
            }
        }
    }
}

package be.tapped.vtmgo.content

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.right
import be.tapped.common.internal.executeAsync
import be.tapped.vtmgo.ApiResponse
import be.tapped.vtmgo.common.HeaderBuilder
import be.tapped.vtmgo.common.safeBodyString
import be.tapped.vtmgo.profile.JWT
import be.tapped.vtmgo.profile.Profile
import be.tapped.vtmgo.profile.VTMGOProduct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

internal class JsonFavoritesParser {
    fun parse(json: String): Either<ApiResponse.Failure, Favorite> =
            Either.catch { Json.decodeFromString<Favorite>(json) }.mapLeft { ApiResponse.Failure.JsonParsingException(it, json) }
}

public sealed interface FavoritesRepo {
    public suspend fun fetchMyFavorites(jwt: JWT, profile: Profile): Either<ApiResponse.Failure, ApiResponse.Success.Content.Favorites>
}

internal class HttpFavoritesRepo(
        private val client: OkHttpClient,
        private val baseContentHttpUrlBuilder: BaseContentHttpUrlBuilder,
        private val headerBuilder: HeaderBuilder,
        private val jsonFavoritesParser: JsonFavoritesParser,
) : FavoritesRepo {

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
    // -H "Cookie:authId=44de1089-dac7-43a8-a7b5-0f01042ab769" \
    // -H "User-Agent:okhttp/4.9.0" "https://lfvp-api.dpgmedia.net/vtmgo/main/swimlane/my-list"
    override suspend fun fetchMyFavorites(
            jwt: JWT,
            profile: Profile,
    ): Either<ApiResponse.Failure, ApiResponse.Success.Content.Favorites> {
        fun constructUrl(product: VTMGOProduct): HttpUrl =
                baseContentHttpUrlBuilder.constructBaseContentUrl(product).addPathSegment("my-list").build()

        return withContext(Dispatchers.IO) {
            val response = client.executeAsync(
                    Request.Builder().headers(headerBuilder.authenticationHeaders(jwt, profile)).get().url(constructUrl(profile.product)).build()
            )

            either {
                val json = response.safeBodyString().bind()
                (if (json.isBlank()) ApiResponse.Success.Content.Favorites(null).right()
                else jsonFavoritesParser.parse(json).map { ApiResponse.Success.Content.Favorites(it).right() }.bind()).bind()
            }
        }
    }
}

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
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

internal class JsonCategoryParser {
    fun parse(json: String): Either<ApiResponse.Failure, CategoryResponse> =
            Either.catch<CategoryResponse> { Json.decodeFromString(json) }.mapLeft { JsonParsingException(it, json) }
}

public sealed interface CategoryRepo {
    public suspend fun fetchCategories(jwt: JWT, profile: Profile): Either<ApiResponse.Failure, ApiResponse.Success.Content.Categories>
}

internal class HttpCategoryRepo(
        private val client: OkHttpClient,
        private val baseContentHttpUrlBuilder: BaseContentHttpUrlBuilder,
        private val headerBuilder: HeaderBuilder,
        private val jsonCategoryParser: JsonCategoryParser,
) : CategoryRepo {

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
    // -H "User-Agent:okhttp/4.9.0" "https://lfvp-api.dpgmedia.net/vtmgo/catalog/filters?pageSize=2000"
    override suspend fun fetchCategories(jwt: JWT, profile: Profile): Either<ApiResponse.Failure, ApiResponse.Success.Content.Categories> =
            withContext(Dispatchers.IO) {
                val response = client.executeAsync(
                        Request.Builder()
                                .headers(headerBuilder.authenticationHeaders(jwt, profile))
                                .get()
                                .url(constructUrl(profile.product))
                                .build()
                )

                either {
                    val categoryResponse = jsonCategoryParser.parse(response.safeBodyString().bind()).bind()
                    ApiResponse.Success.Content.Categories(categoryResponse.categories)
                }
            }

    private fun constructUrl(vtmGoProduct: VTMGOProduct): HttpUrl =
            baseContentHttpUrlBuilder.constructBaseContentUrl(vtmGoProduct)
                    .addPathSegments("catalog/filters")
                    .addQueryParameter("pageSize", "2000")
                    .build()
}

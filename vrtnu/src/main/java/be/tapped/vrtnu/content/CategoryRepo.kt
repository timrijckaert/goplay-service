package be.tapped.vrtnu.content

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.flatMap
import be.tapped.common.executeAsync
import be.tapped.common.validateResponse
import be.tapped.vrtnu.ApiResponse
import be.tapped.vrtnu.ApiResponse.Failure.JsonParsingException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import okhttp3.OkHttpClient
import okhttp3.Request

internal class JsonCategoryParser {
    suspend fun parse(json: String): Either<ApiResponse.Failure, List<Category>> =
        Either.fromNullable(Json.decodeFromString<JsonObject>(json)["items"]?.jsonArray)
            .mapLeft { ApiResponse.Failure.EmptyJson }
            .flatMap {
                Either.catch {
                    Json.decodeFromJsonElement<List<Category>>(it)
                }.mapLeft(::JsonParsingException)
            }
}

interface CategoryRepo {
    suspend fun fetchCategories(): Either<ApiResponse.Failure, ApiResponse.Success.Content.Categories>
}

internal class HttpCategoryRepo(
    private val client: OkHttpClient,
    private val jsonCategoryParser: JsonCategoryParser,
) : CategoryRepo {
    companion object {
        private const val CATEGORIES_URL = "https://www.vrt.be/vrtnu/categorieen/jcr:content/par/categories.model.json"
    }

    override suspend fun fetchCategories(): Either<ApiResponse.Failure, ApiResponse.Success.Content.Categories> =
        withContext(Dispatchers.IO) {
            val categoryResponse = client.executeAsync(
                Request.Builder()
                    .get()
                    .url(CATEGORIES_URL)
                    .build()
            )

            either {
                !categoryResponse.validateResponse { ApiResponse.Failure.NetworkFailure(categoryResponse.code, categoryResponse.request) }
                val rawJson = !Either.fromNullable(categoryResponse.body).mapLeft { ApiResponse.Failure.EmptyJson }
                ApiResponse.Success.Content.Categories(!jsonCategoryParser.parse(rawJson.string()))
            }
        }
}

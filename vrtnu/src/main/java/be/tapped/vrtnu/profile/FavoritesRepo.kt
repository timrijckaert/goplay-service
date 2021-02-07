package be.tapped.vrtnu.profile

import arrow.core.Either
import arrow.core.computations.either
import be.tapped.common.internal.executeAsync
import be.tapped.vrtnu.ApiResponse
import be.tapped.vrtnu.common.safeBodyString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*
import okhttp3.OkHttpClient
import okhttp3.Request

public data class FavoriteWrapper(private val favorites: Map<String, Favorite>) {
    public val size: Int = favorites.size
    public val programs: Set<String> = favorites.keys
    public val currentFavorites: List<Favorite> = favorites.values.filter(Favorite::isFavorite)
    public val nonFavorites: List<Favorite> = favorites.values.filterNot(Favorite::isFavorite)
    private val nonWordCharacterRegex = Regex("\\W")
    private fun sanitizeProgramName(rawProgramName: String) = nonWordCharacterRegex.replace(rawProgramName, "").toLowerCase()
    public operator fun get(programName: String): Favorite? = favorites["vrtnuaz${sanitizeProgramName(programName)}"]
}

@Serializable
public data class Favorite(
        val created: Long = -1,
        val updated: Long = -1,
        val adobeCloudId: String? = null,
        val isFavorite: Boolean,
        val programUrl: String,
        val title: String,
        val whatsonId: String,
)

internal class JsonFavoriteParser {
    suspend fun parse(json: String): Either<ApiResponse.Failure, FavoriteWrapper> = Either.catch {
        val rootJsonObject = Json.decodeFromString<JsonObject>(json)
        FavoriteWrapper(rootJsonObject.keys.associateWith {
            val favoriteJson = rootJsonObject[it]!!.jsonObject
            val created = favoriteJson["created"]!!.jsonPrimitive.long
            val updated = favoriteJson["updated"]!!.jsonPrimitive.long
            val valueJsonObject = favoriteJson["value"]!!.jsonObject
            Json.decodeFromJsonElement<Favorite>(valueJsonObject).copy(created = created, updated = updated)
        })
    }.mapLeft(ApiResponse.Failure::JsonParsingException)
}

public sealed interface FavoritesRepo {
    public suspend fun favorites(xVRTToken: XVRTToken): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.Favorites>
}

internal class HttpFavoritesRepo(
        private val client: OkHttpClient,
        private val jsonFavoriteParser: JsonFavoriteParser,
) : FavoritesRepo {

    companion object {
        private const val FAVORITES_URL = "https://video-user-data.vrt.be/favorites"
    }

    //curl \
    // -H 'Host: video-user-data.vrt.be' \
    // -H 'Content-Type: application/json' \
    // -H 'Authorization: Bearer <xVRTToken>' \
    // 'https://video-user-data.vrt.be/favorites'
    override suspend fun favorites(xVRTToken: XVRTToken): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.Favorites> =
            withContext(Dispatchers.IO) {
                val favoritesResponse = client.executeAsync(
                        Request.Builder().get().header("Authorization", "Bearer ${xVRTToken.token}").url(FAVORITES_URL).build()
                )

                either {
                    ApiResponse.Success.Authentication.Favorites(!jsonFavoriteParser.parse(!favoritesResponse.safeBodyString()))
                }
            }
}

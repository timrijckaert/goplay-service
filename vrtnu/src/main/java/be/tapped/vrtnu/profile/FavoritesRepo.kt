package be.tapped.vrtnu.profile

import arrow.core.Either
import arrow.core.computations.either
import be.tapped.vtmgo.common.executeAsync
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import okhttp3.OkHttpClient
import okhttp3.Request

data class FavoriteWrapper(private val favorites: Map<String, Favorite>) {
    private val nonWordCharacterRegex = Regex("\\W")
    private fun sanitizeProgramName(rawProgramName: String) = nonWordCharacterRegex.replace(rawProgramName, "").toLowerCase()
    operator fun get(programName: String): Favorite? = favorites["vrtnuaz${sanitizeProgramName(programName)}"]
}

@Serializable
data class Favorite(
    val created: Long = -1,
    val updated: Long = -1,
    val adobeCloudId: String? = null,
    val isFavorite: Boolean,
    val programUrl: String,
    val title: String,
    val whatsonId: String,
)

internal class JsonFavoriteParser {
    suspend fun parse(json: String): Either<ProfileResponse.Failure, FavoriteWrapper> =
        Either.catch {
            val rootJsonObject = Json.decodeFromString<JsonObject>(json)
            FavoriteWrapper(rootJsonObject.keys.associateWith {
                val favoriteJson = rootJsonObject[it]!!.jsonObject
                val created = favoriteJson["created"]!!.jsonPrimitive.long
                val updated = favoriteJson["updated"]!!.jsonPrimitive.long
                val valueJsonObject = favoriteJson["value"]!!.jsonObject
                Json.decodeFromJsonElement<Favorite>(valueJsonObject).copy(created = created, updated = updated)
            })
        }.mapLeft(ProfileResponse.Failure::JsonParsingException)
}

interface FavoritesRepo {
    suspend fun favorites(xVRTToken: XVRTToken): Either<ProfileResponse.Failure, ProfileResponse.Success.Favorites>
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
    override suspend fun favorites(xVRTToken: XVRTToken): Either<ProfileResponse.Failure, ProfileResponse.Success.Favorites> =
        withContext(Dispatchers.IO) {
            val favoritesResponse = client.executeAsync(
                Request.Builder()
                    .get()
                    .header("Authorization", "Bearer ${xVRTToken.token}")
                    .url(FAVORITES_URL)
                    .build()
            ).body

            either {
                val favoritesJson = !Either.fromNullable(favoritesResponse?.string()).mapLeft { ProfileResponse.Failure.EmptyJson }
                ProfileResponse.Success.Favorites(!jsonFavoriteParser.parse(favoritesJson))
            }
        }
}

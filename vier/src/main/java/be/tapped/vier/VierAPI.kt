package be.tapped.vier

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import okhttp3.OkHttpClient
import okhttp3.Request

@Deprecated("Check if we need this")
public class VierAPI {
    private val client = OkHttpClient.Builder().build()

    public fun getContentTree(accessToken: String): JsonObject =
        Json.decodeFromString(
            client.newCall(
                Request.Builder()
                    .get()
                    .url(CONTENT_TREE)
                    .header("cookie", constructDidomiToken(accessToken))
                    .build()
            ).execute().body!!.string()
        )

    private fun constructDidomiToken(accessToken: String) = "didomi_token=$accessToken"

    public companion object {
        private const val BASE_VIER_API = "https://www.vier.be/api"
        private const val CONTENT_TREE = "$BASE_VIER_API/content_tree"
    }
}

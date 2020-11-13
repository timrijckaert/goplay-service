package be.tapped.vtmgo.authentication

import be.tapped.vtmgo.common.executeAsync
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request

interface ProfileRepo {
    suspend fun getProfiles(jwtToken: JWT): List<Profile>
}

internal class JsonProfileParser {
    fun parse(json: JsonElement): Profile {
        val profile = json.jsonObject
        val color = profile["color"]!!.jsonObject
        return Profile(
            id = profile["id"]!!.jsonPrimitive.content,
            VTMGOProducts = VTMGOProducts.valueOf(profile["product"]!!.jsonPrimitive.content),
            name = profile["name"]!!.jsonPrimitive.content,
            gender = profile["gender"]!!.jsonPrimitive.content,
            birthDate = profile["birthDate"]!!.jsonPrimitive.content,
            color = color["start"]!!.jsonPrimitive.content,
            color2 = color["end"]!!.jsonPrimitive.content,
        )
    }
}

internal class HttpProfileRepo(
    private val client: OkHttpClient,
    private val jsonProfileParser: JsonProfileParser,
    private val headerBuilder: HeaderBuilder,
) : ProfileRepo {
    companion object {
        private const val API_ENDPOINT = "https://lfvp-api.dpgmedia.net"
    }

    override suspend fun getProfiles(jwtToken: JWT): List<Profile> {
        val profiles = client.executeAsync(
            Request.Builder()
                .get()
                .headers(headerBuilder.authenticationHeaders(jwtToken))
                .url("${API_ENDPOINT}/profiles?products=VTM_GO,VTM_GO_KIDS")
                .build()
        )

        return Json.decodeFromString<JsonArray>(profiles.body!!.string()).map(jsonProfileParser::parse)
    }
}

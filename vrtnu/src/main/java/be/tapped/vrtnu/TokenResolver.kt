package be.tapped.vrtnu

import be.tapped.vrtnu.model.VRTToken
import be.tapped.vrtnu.model.VRTLogin
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.Exception

object TokenResolver {

    private const val API_KEY = "3_qhEcPa5JGFROVwu5SWKqJ4mVOIkwlFNMSKwzPDAh8QZOtHqu6L4nD5Q7lk0eXOOG"
    private const val LOGIN_URL = "https://accounts.vrt.be/accounts.login"
    private const val TOKEN_GATEWAY_URL = "https://token.vrt.be"

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private val client = OkHttpClient()

    fun login(userName: String, password: String): VRTLogin =
        try {
            val loginJson = getLoginJson(userName, password)
            if (loginJson["errorCode"]!!.jsonPrimitive.int != 0) {
                when (loginJson["errorDetails"]?.jsonPrimitive?.content) {
                    "invalid loginID or password" -> VRTLogin.Failure.InvalidCredentials
                    "loginID must be provided" -> VRTLogin.Failure.MissingLoginId
                    "Missing required parameter: password" -> VRTLogin.Failure.MissingPassword
                    else -> VRTLogin.Failure.Unknown
                }
            } else {
                val vrtToken = getNewToken("X-VRT-Token", userName, loginJson)
                if (vrtToken != null) {
                    VRTLogin.Success.OK(vrtToken)
                } else {
                    VRTLogin.Failure.Unknown
                }
            }
        } catch (e: Exception) {
            VRTLogin.Failure.Exceptionally(e)
        }

    private fun getNewToken(
        name: String,
        userName: String,
        loginJson: JsonObject
    ): VRTToken? =
        when (name) {
            "X-VRT-Token" -> getXVRTToken(userName, loginJson)
            else -> throw IllegalArgumentException("Not handled $name")
        }

    private fun getXVRTToken(userName: String, loginJson: JsonObject): VRTToken? {
        val loginToken =
            loginJson["sessionInfo"]!!.jsonObject["login_token"]?.jsonPrimitive?.content
                ?: throw IllegalStateException("No login token found!")

        val loginCookie = "glt_${API_KEY}=$loginToken"
        val json = buildJsonObject {
            put("uid", loginJson["UID"]!!.jsonPrimitive.content)
            put("uidsig", loginJson["UIDSignature"]!!.jsonPrimitive.content)
            put("ts", loginJson["signatureTimestamp"]!!.jsonPrimitive.content)
            put("email", userName)
        }.toString()

        val response = client.newCall(
            Request.Builder()
                .url(TOKEN_GATEWAY_URL)
                .addHeader("Cookie", loginCookie)
                .post(json.toRequestBody(jsonMediaType))
                .build()
        ).execute()
        return createTokenDictionaryFromHeader(response.headers["Set-Cookie"])
    }

    private fun createTokenDictionaryFromHeader(rawCookieHeader: String?): VRTToken? =
        rawCookieHeader?.let {
            val cookieDataArr = it.split("X-VRT-Token=")[1].split("; ")
            val expiresRegex =
                Regex("([A-Za-z]{3}, \\d{2} [A-Za-z]{3} \\d{4} \\d{2}:\\d{2}:\\d{2} [A-Za-z]{3})")
            val expires = expiresRegex.find(cookieDataArr[2])!!.groups.first()!!.value
            VRTToken(
                cookieName = cookieDataArr[0],
                expirationDate = expires
            )
        }

    private fun getLoginJson(userName: String, password: String): JsonObject {
        val requestBody = FormBody.Builder()
            .add("loginID", userName)
            .add("password", password)
            .add("sessionExpiration", "-1")
            .add("APIKey", API_KEY)
            .add("targetEnv", "jssdk")
            .build()

        val response = client.newCall(
            Request.Builder()
                .url(LOGIN_URL)
                .post(requestBody)
                .build()
        )
            .execute()

        return Json.decodeFromString(response.body!!.string())
    }
}

package be.tapped.vrtnu.authentication

import arrow.core.Either
import arrow.core.computations.either
import be.tapped.vrtnu.authentication.ProfileResponse.Failure.FailedToLogin
import be.tapped.vtmgo.common.executeAsync
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

data class LoginFailure(
    val errorCode: Int,
    private val errorDetails: String?,
) {
    enum class LoginFailure {
        INVALID_CREDENTIALS,
        MISSING_LOGIN_ID,
        MISSING_PASSWORD,
        UNKNOWN
    }

    val isValid = errorCode == 0
    val loginFailure = when (errorDetails) {
        "invalid loginID or password" -> LoginFailure.INVALID_CREDENTIALS
        "loginID must be provided" -> LoginFailure.MISSING_LOGIN_ID
        "Missing required parameter: password" -> LoginFailure.MISSING_PASSWORD
        else                                   -> LoginFailure.UNKNOWN
    }
}

@Serializable
data class LoginResponse(
    val loginToken: String?,
    val uid: String,
    val uidSignature: String,
    val signatureTimestamp: String,
)

internal object JsonLoginResponseMapper {
    suspend fun parse(loginJson: JsonObject): Either<LoginFailure, LoginResponse> =
        Either.catch {
            LoginResponse(
                loginJson["sessionInfo"]!!.jsonObject["login_token"]?.jsonPrimitive?.content,
                loginJson["UID"]!!.jsonPrimitive.content,
                loginJson["UIDSignature"]!!.jsonPrimitive.content,
                loginJson["signatureTimestamp"]!!.jsonPrimitive.content,
            )
        }.mapLeft {
            LoginFailure(
                loginJson["errorCode"]!!.jsonPrimitive.int,
                loginJson["errorDetails"]?.jsonPrimitive?.content
            )
        }
}

interface LoginRepo {
    suspend fun fetchLoginResponse(userName: String, password: String): Either<ProfileResponse.Failure, LoginResponse>
}

internal class HttpLoginRepo(
    private val client: OkHttpClient,
    private val jsonLoginResponseMapper: JsonLoginResponseMapper,
) : LoginRepo {
    companion object {
        private const val API_KEY = "3_qhEcPa5JGFROVwu5SWKqJ4mVOIkwlFNMSKwzPDAh8QZOtHqu6L4nD5Q7lk0eXOOG"
        private const val LOGIN_URL = "https://accounts.vrt.be/accounts.login"
    }

    override suspend fun fetchLoginResponse(userName: String, password: String): Either<ProfileResponse.Failure, LoginResponse> =
        withContext(Dispatchers.IO) {
            val loginJson = client.executeAsync(
                Request.Builder()
                    .url(LOGIN_URL)
                    .post(
                        FormBody.Builder()
                            .add("loginID", userName)
                            .add("password", password)
                            .add("sessionExpiration", "-2")
                            .add("APIKey", API_KEY)
                            .add("targetEnv", "jssdk")
                            .build()
                    )
                    .build()
            ).body?.string()

            either {
                val rawLoginJson = !Either.fromNullable(loginJson).mapLeft { ProfileResponse.Failure.EmptyJson }
                val loginResponse = jsonLoginResponseMapper.parse(Json.decodeFromString(rawLoginJson))

                !loginResponse.mapLeft(::FailedToLogin)
            }
        }
}

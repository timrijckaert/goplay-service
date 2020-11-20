package be.tapped.vrtnu.authentication

import arrow.core.Either
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

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

package be.tapped.vrtnu.authentication

import arrow.core.Either
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

data class LoginResponse(
    val errorCode: Int,
    val errorDetails: String?,
    val loginToken: String?,
    val uid: String,
    val uidSignature: String,
    val signatureTimestamp: String,
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

internal class JsonLoginResponseMapper {
    suspend fun parse(loginJson: JsonObject): Either<Throwable, LoginResponse> =
        Either.catch {
            LoginResponse(
                loginJson["errorCode"]!!.jsonPrimitive.int,
                loginJson["errorDetails"]?.jsonPrimitive?.content,
                loginJson["sessionInfo"]!!.jsonObject["login_token"]?.jsonPrimitive?.content,
                loginJson["UID"]!!.jsonPrimitive.content,
                loginJson["UIDSignature"]!!.jsonPrimitive.content,
                loginJson["signatureTimestamp"]!!.jsonPrimitive.content,
            )
        }
}

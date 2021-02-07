package be.tapped.vrtnu.profile

import arrow.core.Either
import arrow.core.computations.either
import be.tapped.common.internal.executeAsync
import be.tapped.vrtnu.ApiResponse
import be.tapped.vrtnu.common.safeBodyString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

@Serializable
public data class LoginFailure(
        val callId: String,
        val errorCode: Int,
        val errorDetails: String?,
        val errorMessage: String,
        val apiVersion: Int,
        val statusCode: Int,
        val statusReason: String,
        val time: String,
) {

    public enum class Type {
        INVALID_CREDENTIALS,
        MISSING_LOGIN_ID,
        MISSING_PASSWORD,
        UNKNOWN
    }

    public val isValid: Boolean get() = errorCode == 0
    public val loginFailureType: Type
        get() = when (errorDetails) {
            "invalid loginID or password" -> Type.INVALID_CREDENTIALS
            "loginID must be provided" -> Type.MISSING_LOGIN_ID
            "Missing required parameter: password" -> Type.MISSING_PASSWORD
            else -> Type.UNKNOWN
        }
}


@Serializable
public data class Profile(
        val firstName: String,
        val lastName: String,
        val age: Int,
        val birthDay: Int,
        val birthMonth: Int,
        val birthYear: Int,
        val city: String,
        val country: String,
        val email: String,
        val gender: String,
        val zip: String,
)

@Serializable
public data class SessionInfo(@SerialName("login_token") val loginToken: String)

@Serializable
public data class LoginResponse(
        val callId: String,
        val errorCode: Int,
        val apiVersion: Int,
        val statusCode: Int,
        val statusReason: String,
        val time: String,
        val registeredTimestamp: Long,
        @SerialName("UID") val uid: String,
        @SerialName("UIDSignature") val uidSignature: String,
        val signatureTimestamp: String,
        val created: String,
        val createdTimestamp: Long,
        val isActive: Boolean,
        val isRegistered: Boolean,
        val isVerified: Boolean,
        val lastLogin: String,
        val lastLoginTimestamp: Long,
        val lastUpdated: String,
        val lastUpdatedTimestamp: Long,
        val loginProvider: String,
        val oldestDataUpdated: String,
        val oldestDataUpdatedTimestamp: Long,
        val profile: Profile,
        val registered: String,
        val socialProviders: String,
        val verified: String,
        val verifiedTimestamp: Long,
        val newUser: Boolean,
        val sessionInfo: SessionInfo,
)

internal object JsonLoginResponseMapper {
    fun parse(json: String): Either<LoginFailure, LoginResponse> =
            Either.catch { Json.decodeFromString<LoginResponse>(json) }.mapLeft { Json.decodeFromString(json) }
}

public sealed interface LoginRepo {
    public suspend fun fetchLoginResponse(userName: String, password: String): Either<ApiResponse.Failure, LoginResponse>
}

internal class HttpLoginRepo(
        private val client: OkHttpClient,
        private val jsonLoginResponseMapper: JsonLoginResponseMapper,
) : LoginRepo {
    companion object {
        private const val API_KEY = "3_qhEcPa5JGFROVwu5SWKqJ4mVOIkwlFNMSKwzPDAh8QZOtHqu6L4nD5Q7lk0eXOOG"
        private const val LOGIN_URL = "https://accounts.vrt.be/accounts.login"
    }

    override suspend fun fetchLoginResponse(userName: String, password: String): Either<ApiResponse.Failure, LoginResponse> =
            withContext(Dispatchers.IO) {
                val loginResponse = client.executeAsync(
                        Request.Builder().url(LOGIN_URL).post(
                                FormBody.Builder().add("loginID", userName).add("password", password).add("sessionExpiration", "-2").add("APIKey", API_KEY)
                                        .add("targetEnv", "jssdk").build()
                        ).build()
                )

                either {
                    !jsonLoginResponseMapper.parse(!loginResponse.safeBodyString()).mapLeft(ApiResponse.Failure.Authentication::FailedToLogin)
                }
            }
}

package be.tapped.vrtnu.authentication

import arrow.core.Either
import arrow.core.computations.either
import be.tapped.vrtnu.authentication.TokenRepo.TokenResponse.Failure.FailedToLogin
import be.tapped.vtmgo.common.executeAsync
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

interface LoginRepo {
    suspend fun fetchLoginResponse(userName: String, password: String): Either<TokenRepo.TokenResponse.Failure, LoginResponse>
}

internal class HttpLoginRepo(
    private val client: OkHttpClient,
    private val jsonLoginResponseMapper: JsonLoginResponseMapper,
) : LoginRepo {
    companion object {
        private const val API_KEY = "3_qhEcPa5JGFROVwu5SWKqJ4mVOIkwlFNMSKwzPDAh8QZOtHqu6L4nD5Q7lk0eXOOG"
        private const val LOGIN_URL = "https://accounts.vrt.be/accounts.login"
    }

    override suspend fun fetchLoginResponse(userName: String, password: String): Either<TokenRepo.TokenResponse.Failure, LoginResponse> =
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
                val rawLoginJson = !Either.fromNullable(loginJson).mapLeft { TokenRepo.TokenResponse.Failure.EmptyJson }
                val loginResponse = jsonLoginResponseMapper.parse(Json.decodeFromString(rawLoginJson))

                !loginResponse.mapLeft(::FailedToLogin)
            }
        }
}

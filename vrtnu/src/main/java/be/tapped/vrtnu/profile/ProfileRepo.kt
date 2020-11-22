package be.tapped.vrtnu.profile

import arrow.core.NonEmptyList
import be.tapped.vrtnu.common.defaultOkHttpClient
import be.tapped.vtmgo.common.DefaultCookieJar
import be.tapped.vtmgo.common.ReadOnlyCookieJar
import okhttp3.OkHttpClient

sealed class ProfileResponse {
    sealed class Success : ProfileResponse() {
        data class Token(val tokenWrapper: TokenWrapper) : Success()
        data class PlayerToken(val vrtPlayerToken: VRTPlayerToken) : Success()
        data class VRTToken(val xVRTToken: XVRTToken) : Success()
    }

    sealed class Failure : ProfileResponse() {
        data class JsonParsingException(val throwable: Throwable) : Failure()
        data class FailedToLogin(val loginResponseFailure: LoginFailure) : Failure()
        data class MissingCookieValues(val cookieValues: NonEmptyList<String>) : Failure()
        object EmptyJson : Failure()
    }
}

class ProfileRepo(
    cookieJar: ReadOnlyCookieJar = DefaultCookieJar(),
    client: OkHttpClient = defaultOkHttpClient.newBuilder().cookieJar(cookieJar).build(),
    tokenRepo: TokenRepo = HttpTokenRepo(client, cookieJar),
) : TokenRepo by tokenRepo

package be.tapped.vrtnu.authentication

import arrow.core.Either
import be.tapped.vrtnu.authentication.TokenRepo.TokenResponse.Failure.MissingCookieValues
import be.tapped.vtmgo.common.ReadOnlyCookieJar
import be.tapped.vtmgo.common.executeAsync
import okhttp3.OkHttpClient
import okhttp3.Request

interface OIDCXSRFRepo {
    suspend fun fetchXSRFToken(): Either<MissingCookieValues, OIDCXSRF>
}

internal class HttpOIDCXSRFRepo(
    private val client: OkHttpClient,
    private val cookieJar: ReadOnlyCookieJar,
) : OIDCXSRFRepo {

    companion object {
        private const val USER_TOKEN_GATEWAY_URL = "https://token.vrt.be/vrtnuinitlogin?provider=site&destination=https://www.vrt.be/vrtnu/"
        private const val COOKIE_XSRF = "OIDCXSRF"
    }

    override suspend fun fetchXSRFToken(): Either<MissingCookieValues, OIDCXSRF> {
        client.executeAsync(
            Request.Builder()
                .get()
                .url(USER_TOKEN_GATEWAY_URL)
                .build()
        )

        return cookieJar.validateCookie(COOKIE_XSRF).map(::OIDCXSRF).mapLeft(::MissingCookieValues).toEither()
    }
}

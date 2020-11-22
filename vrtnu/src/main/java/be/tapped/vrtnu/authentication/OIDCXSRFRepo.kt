package be.tapped.vrtnu.authentication

import arrow.core.Either
import be.tapped.vrtnu.authentication.ProfileResponse.Failure.MissingCookieValues
import be.tapped.vtmgo.common.ReadOnlyCookieJar
import be.tapped.vtmgo.common.executeAsync
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.closeQuietly

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

    override suspend fun fetchXSRFToken(): Either<MissingCookieValues, OIDCXSRF> =
        withContext(Dispatchers.IO) {
            client.executeAsync(
                Request.Builder()
                    .get()
                    .url(USER_TOKEN_GATEWAY_URL)
                    .build()
            ).closeQuietly()

            cookieJar.validateCookie(COOKIE_XSRF).map(::OIDCXSRF).mapLeft(::MissingCookieValues).toEither()
        }
}

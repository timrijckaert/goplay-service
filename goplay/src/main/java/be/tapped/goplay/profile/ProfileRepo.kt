package be.tapped.goplay.profile

import arrow.core.Either
import arrow.core.Validated
import arrow.core.flatMap
import arrow.core.invalid
import arrow.core.valid
import be.tapped.goplay.ApiResponse
import be.tapped.goplay.ApiResponse.Failure.Authentication.*
import be.tapped.goplay.ApiResponse.Failure.Authentication.Profile
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider
import software.amazon.awssdk.core.SdkResponse
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserRequest
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserResponse

public class ProfileUserAttributeParser {
    public fun parse(userResponse: GetUserResponse): ApiResponse.Success.Authentication.Profile {
        val userAttributeMap =
            userResponse.userAttributes().groupBy(AttributeType::name, AttributeType::value).mapValues { (_, value) -> value.firstOrNull() }

        return ApiResponse.Success.Authentication.Profile(
            Profile(
                username = userResponse.username(),
                sub = userAttributeMap["sub"],
                birthDate = userAttributeMap["birthdate"],
                gender = userAttributeMap["gender"],
                postalCode = userAttributeMap["custom:postal_code"],
                selligentId = userAttributeMap["custom:selligentId"],
                name = userAttributeMap["name"],
                familyName = userAttributeMap["family_name"],
                email = userAttributeMap["email"]
            )
        )
    }
}

public interface ProfileRepo {

    public suspend fun fetchTokens(username: String, password: String): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.Token>

    public suspend fun refreshTokens(refreshToken: RefreshToken): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.Token>

    public suspend fun getUserAttributes(accessToken: AccessToken): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.Profile>

}

internal val <T : SdkResponse> T.checkResult: Validated<ApiResponse.Failure, T>
    get() = if (sdkHttpResponse().isSuccessful) {
        valid()
    } else {
        AWS(sdkHttpResponse().statusCode(), sdkHttpResponse().statusText().orElse(null)).invalid()
    }

public class HttpProfileRepo(private val profileUserAttributeParser: ProfileUserAttributeParser = ProfileUserAttributeParser()) : ProfileRepo {

    private val cognitoIdentityProvider by lazy {
        CognitoIdentityProviderClient.builder().credentialsProvider(AnonymousCredentialsProvider.create()).region(Region.EU_WEST_1).build()
    }

    override suspend fun fetchTokens(username: String, password: String): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.Token> =
        cognitoIdentityProvider.initiateAuth(AuthenticationHelper.initiateUserSrpAuthRequest(username)).checkResult.toEither()
            .flatMap { cognitoIdentityProvider.respondToAuthChallenge(AuthenticationHelper.userSrpAuthRequest(it, password)).checkResult.toEither() }
            .map {
                val authenticationResult = it.authenticationResult()
                ApiResponse.Success.Authentication.Token(
                    TokenWrapper(
                        accessToken = AccessToken(authenticationResult.accessToken()),
                        expiry = Expiry(System.currentTimeMillis() + (authenticationResult.expiresIn() * 1000)),
                        tokenType = authenticationResult.tokenType(),
                        refreshToken = RefreshToken(authenticationResult.refreshToken()),
                        idToken = IdToken(authenticationResult.idToken())
                    )
                )
            }.mapLeft { Login }

    override suspend fun refreshTokens(refreshToken: RefreshToken): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.Token> =
        cognitoIdentityProvider.initiateAuth(AuthenticationHelper.refreshToken(refreshToken.token)).checkResult.toEither().map {
            with(it.authenticationResult()) {
                ApiResponse.Success.Authentication.Token(
                    TokenWrapper(
                        accessToken = AccessToken(accessToken()),
                        expiry = Expiry(System.currentTimeMillis() + (expiresIn() * 1000)),
                        tokenType = tokenType(),
                        refreshToken = refreshToken()?.let(::RefreshToken) ?: refreshToken,
                        idToken = IdToken(idToken())
                    )
                )
            }
        }.mapLeft { Refresh }

    override suspend fun getUserAttributes(accessToken: AccessToken): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.Profile> =
        cognitoIdentityProvider.getUser(GetUserRequest.builder().accessToken(accessToken.token).build()).checkResult.toEither()
            .map(profileUserAttributeParser::parse).mapLeft { Profile }

}

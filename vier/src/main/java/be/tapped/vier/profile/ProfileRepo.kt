package be.tapped.vier.profile

import arrow.core.*
import arrow.core.computations.either
import be.tapped.vier.ApiResponse
import be.tapped.vier.ApiResponse.Failure.Authentication.*
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider
import software.amazon.awssdk.core.SdkResponse
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserRequest
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserResponse

public class ProfileUserAttributeParser {
    public fun parse(userResponse: GetUserResponse): ApiResponse.Success.Authentication.Profile {
        val userAttributeMap = userResponse.userAttributes()
            .groupBy(AttributeType::name, AttributeType::value)
            .mapValues { (_, value) -> value.firstOrNull() }

        return ApiResponse.Success.Authentication.Profile(
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
    }
}

public interface ProfileRepo {

    public suspend fun fetchTokens(username: String, password: String): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.Token>

    public suspend fun refreshTokens(refreshToken: String): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.Token>

    public suspend fun getUserAttributes(accessToken: String): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.Profile>

}

internal val <T : SdkResponse> T.checkResult: Validated<ApiResponse.Failure, T>
    get() =
        if (sdkHttpResponse().isSuccessful) {
            valid()
        } else {
            AWS(sdkHttpResponse().statusCode(), sdkHttpResponse().statusText().orElse(null)).invalid()
        }

public class HttpProfileRepo(private val profileUserAttributeParser: ProfileUserAttributeParser = ProfileUserAttributeParser()) : ProfileRepo {

    private val cognitoIdentityProvider by lazy {
        CognitoIdentityProviderClient.builder()
            .credentialsProvider(AnonymousCredentialsProvider.create())
            .region(Region.EU_WEST_1)
            .build()
    }

    override suspend fun fetchTokens(username: String, password: String): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.Token> =
        cognitoIdentityProvider.initiateAuth(AuthenticationHelper.initiateUserSrpAuthRequest(username)).checkResult.toEither()
            .flatMap { cognitoIdentityProvider.respondToAuthChallenge(AuthenticationHelper.userSrpAuthRequest(it, password)).checkResult.toEither() }
            .map {
                val authenticationResult = it.authenticationResult()
                ApiResponse.Success.Authentication.Token(
                    accessToken = authenticationResult.accessToken(),
                    expiresIn = authenticationResult.expiresIn(),
                    tokenType = authenticationResult.tokenType(),
                    refreshToken = authenticationResult.refreshToken(),
                    idToken = authenticationResult.idToken()
                )
            }
            .mapLeft { Login }

    override suspend fun refreshTokens(refreshToken: String): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.Token> =
        cognitoIdentityProvider.initiateAuth(AuthenticationHelper.refreshToken(refreshToken)).checkResult.toEither()
            .map {
                with(it.authenticationResult()) {
                    ApiResponse.Success.Authentication.Token(
                        accessToken = accessToken(),
                        expiresIn = expiresIn(),
                        tokenType = tokenType(),
                        refreshToken = refreshToken() ?: refreshToken,
                        idToken = idToken()
                    )
                }
            }
            .mapLeft { Refresh }

    override suspend fun getUserAttributes(accessToken: String): Either<ApiResponse.Failure, ApiResponse.Success.Authentication.Profile> =
        cognitoIdentityProvider.getUser(GetUserRequest.builder().accessToken(accessToken).build()).checkResult.toEither()
            .map(profileUserAttributeParser::parse)
            .mapLeft { Profile }

}

package be.tapped.goplay.profile

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import be.tapped.goplay.Authentication
import be.tapped.goplay.Failure
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider
import software.amazon.awssdk.core.SdkResponse
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserRequest
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserResponse

internal class ProfileUserAttributeParser {
    fun parse(userResponse: GetUserResponse): Authentication.Profile {
        val userAttributeMap =
            userResponse.userAttributes().groupBy(AttributeType::name, AttributeType::value).mapValues { (_, value) -> value.firstOrNull() }

        return Authentication.Profile(
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

internal class HttpProfileRepo(private val profileUserAttributeParser: ProfileUserAttributeParser) : ProfileRepo {

    private val cognitoIdentityProvider by lazy(CognitoIdentityProviderClient.builder().credentialsProvider(AnonymousCredentialsProvider.create()).region(Region.EU_WEST_1)::build)

    override suspend fun fetchTokens(username: String, password: String): Either<Failure, Authentication.Token> =
        either {
            Either.catch {
                val initiateUserSrpAuthRequest =
                    cognitoIdentityProvider.initiateAuth(AuthenticationHelper.initiateUserSrpAuthRequest(username)).checkResult.bind()
                val authChallengeResponse =
                    cognitoIdentityProvider
                        .respondToAuthChallenge(AuthenticationHelper.userSrpAuthRequest(initiateUserSrpAuthRequest, password))
                        .checkResult
                        .bind()
                        .authenticationResult()

                Authentication.Token(
                    TokenWrapper(
                        accessToken = AccessToken(authChallengeResponse.accessToken()),
                        expiry = Expiry(System.currentTimeMillis() + (authChallengeResponse.expiresIn() * 1000)),
                        tokenType = authChallengeResponse.tokenType(),
                        refreshToken = RefreshToken(authChallengeResponse.refreshToken()),
                        idToken = IdToken(authChallengeResponse.idToken())
                    )
                )
            }.mapLeft { Failure.Authentication.Login }.bind()
        }

    override suspend fun refreshTokens(refreshToken: RefreshToken): Either<Failure, Authentication.Token> =
        either {
            Either.catch {
                val initiateAuthResult = cognitoIdentityProvider.initiateAuth(AuthenticationHelper.refreshToken(refreshToken.token)).checkResult.bind().authenticationResult()
                Authentication.Token(
                    TokenWrapper(
                        accessToken = AccessToken(initiateAuthResult.accessToken()),
                        expiry = Expiry(System.currentTimeMillis() + (initiateAuthResult.expiresIn() * 1000)),
                        tokenType = initiateAuthResult.tokenType(),
                        refreshToken = initiateAuthResult.refreshToken()?.let(::RefreshToken) ?: refreshToken,
                        idToken = IdToken(initiateAuthResult.idToken())
                    )
                )
            }.mapLeft { Failure.Authentication.Refresh }.bind()
        }

    override suspend fun getUserAttributes(accessToken: AccessToken): Either<Failure, Authentication.Profile> =
        either {
            Either.catch {
                val user = cognitoIdentityProvider.getUser(GetUserRequest.builder().accessToken(accessToken.token).build()).checkResult.bind()
                profileUserAttributeParser.parse(user)
            }.mapLeft { Failure.Authentication.Profile }.bind()
        }
}

private val <T : SdkResponse> T.checkResult: Either<Failure, T>
    get() = if (sdkHttpResponse().isSuccessful) {
        right()
    } else {
        Failure.Authentication.AWS(sdkHttpResponse().statusCode(), sdkHttpResponse().statusText().orElse(null)).left()
    }

internal actual val httpProfileRepo: ProfileRepo get() = HttpProfileRepo(ProfileUserAttributeParser())

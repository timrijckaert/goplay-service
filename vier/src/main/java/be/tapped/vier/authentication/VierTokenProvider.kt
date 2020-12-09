package be.tapped.vier.authentication

import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserRequest

public class VierTokenProvider(private val authenticationHelper: AuthenticationHelper = AuthenticationHelper()) {

    private val cognitoIdentityProvider = CognitoIdentityProviderClient.builder()
        .credentialsProvider(AnonymousCredentialsProvider.create())
        .region(Region.EU_WEST_1)
        .build()

    public fun getAuthenticationResultType(username: String, password: String): AuthenticationResultType {
        val initiateAuthRequest = authenticationHelper.initiateUserSrpAuthRequest(username)
        val initAuthResult = cognitoIdentityProvider.initiateAuth(initiateAuthRequest)
        val challengeRequest = authenticationHelper.userSrpAuthRequest(initAuthResult, password)
        return cognitoIdentityProvider
            .respondToAuthChallenge(challengeRequest)
            .authenticationResult()
    }

    public fun refreshToken(refreshToken: String): AuthenticationResultType {
        val refreshTokenRequest = authenticationHelper.refreshToken(refreshToken)
        return cognitoIdentityProvider.initiateAuth(refreshTokenRequest).authenticationResult()
    }

    public fun getUserAttributes(accessToken: String): List<AttributeType> =
        cognitoIdentityProvider.getUser(
            GetUserRequest.builder()
                .accessToken(accessToken)
                .build()
        ).userAttributes()
}

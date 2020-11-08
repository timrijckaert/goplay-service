package be.tapped.vier

import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient
import software.amazon.awssdk.services.cognitoidentityprovider.model.*

class VierTokenProvider(private val authenticationHelper: AuthenticationHelper = AuthenticationHelper()) {

    private val cognitoIdentityProvider = CognitoIdentityProviderClient.builder()
        .credentialsProvider(AnonymousCredentialsProvider.create())
        .region(Region.EU_WEST_1)
        .build()

    fun getAuthenticationResultType(username: String, password: String): AuthenticationResultType {
        val initiateAuthRequest = authenticationHelper.initiateUserSrpAuthRequest(username)
        val initAuthResult = cognitoIdentityProvider.initiateAuth(initiateAuthRequest)
        val challengeRequest = authenticationHelper.userSrpAuthRequest(initAuthResult, password)
        return cognitoIdentityProvider
            .respondToAuthChallenge(challengeRequest)
            .authenticationResult()
    }

    fun refreshToken(refreshToken: String): AuthenticationResultType? {
        val refreshTokenRequest = authenticationHelper.refreshToken(refreshToken)
        return cognitoIdentityProvider.initiateAuth(refreshTokenRequest).authenticationResult()
    }

    fun getUserAttributes(accessToken: String): List<AttributeType> =
        cognitoIdentityProvider.getUser(
            GetUserRequest.builder()
                .accessToken(accessToken)
                .build()
        ).userAttributes()
}

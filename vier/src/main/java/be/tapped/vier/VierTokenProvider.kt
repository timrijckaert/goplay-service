package be.tapped.vier

import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient

class VierTokenProvider(private val authenticationHelper: AuthenticationHelper = AuthenticationHelper()) {
    fun login(username: String, password: String) {
        val initiateAuthRequest = authenticationHelper.initiateUserSrpAuthRequest(username)
        val cognitoIdentityProvider = CognitoIdentityProviderClient.builder()
            .credentialsProvider(AnonymousCredentialsProvider.create())
            .region(Region.EU_WEST_1)
            .build()
        val initAuthResult = cognitoIdentityProvider.initiateAuth(initiateAuthRequest)
        val challengeRequest = authenticationHelper.userSrpAuthRequest(initAuthResult, password)
        val result = cognitoIdentityProvider.respondToAuthChallenge(challengeRequest)
    }
}

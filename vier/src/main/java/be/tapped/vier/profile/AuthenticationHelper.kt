package be.tapped.vier.profile

import software.amazon.awssdk.services.cognitoidentityprovider.model.*
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

// Ugly Port of
// https://github.com/aws-samples/aws-cognito-java-desktop-app/blob/3e0ce0c1dce5f02ec4b655ec05645760d379da0b/src/main/java/com/amazonaws/sample/cognitoui/AuthenticationHelper.java
internal object AuthenticationHelper {

    private const val COGNITO_CLIENT_ID = "6s1h851s8uplco5h6mqh1jac8m"
    private const val COGNITO_POOL_ID = "eu-west-1_dViSsKM5Y"

    private const val EPHEMERAL_KEY_LENGTH = 1024
    private val SECURE_RANDOM = SecureRandom.getInstance("SHA1PRNG")
    private const val HEX_N = ("FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD1"
        + "29024E088A67CC74020BBEA63B139B22514A08798E3404DD"
        + "EF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245"
        + "E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7ED"
        + "EE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3D"
        + "C2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F"
        + "83655D23DCA3AD961C62F356208552BB9ED529077096966D"
        + "670C354E4ABC9804F1746C08CA18217C32905E462E36CE3B"
        + "E39E772C180E86039B2783A2EC07A28FB5C55DF06F4C52C9"
        + "DE2BCBF6955817183995497CEA956AE515D2261898FA0510"
        + "15728E5A8AAAC42DAD33170D04507A33A85521ABDF1CBA64"
        + "ECFB850458DBEF0A8AEA71575D060C7DB3970F85A6E1E4C7"
        + "ABF5AE8CDB0933D71E8C94E04A25619DCEE3D2261AD2EE6B"
        + "F12FFA06D98A0864D87602733EC86A64521F2B18177B200C"
        + "BBE117577A615D6C770988C0BAD946E208E24FA074E5AB31"
        + "43DB5BFCE0FD108E4B82D120A93AD2CAFFFFFFFFFFFFFFFF")
    private val g = BigInteger.valueOf(2)
    private val N = BigInteger(HEX_N, 16)
    private val THREAD_MESSAGE_DIGEST: ThreadLocal<MessageDigest> =
        object : ThreadLocal<MessageDigest>() {
            override fun initialValue(): MessageDigest? {
                return try {
                    MessageDigest.getInstance("SHA-256")
                } catch (e: NoSuchAlgorithmException) {
                    throw SecurityException("Exception in authentication", e)
                }
            }
        }
    private const val DERIVED_KEY_INFO = "Caldera Derived Key"
    private const val DERIVED_KEY_SIZE = 16

    private var a: BigInteger
    private var A: BigInteger
    private var k: BigInteger

    init {
        do {
            a = BigInteger(EPHEMERAL_KEY_LENGTH, SECURE_RANDOM).mod(N)
            A = g.modPow(a, N)
        } while (A.mod(N) == BigInteger.ZERO)

        val messageDigest = THREAD_MESSAGE_DIGEST.get()
        messageDigest.reset()
        messageDigest.update(N.toByteArray())
        val digest = messageDigest.digest(g.toByteArray())
        k = BigInteger(1, digest)
    }

    fun userSrpAuthRequest(
        challenge: InitiateAuthResponse,
        password: String,
    ): RespondToAuthChallengeRequest {
        val userIdForSRP = challenge.challengeParameters()["USER_ID_FOR_SRP"]!!
        val usernameInternal = challenge.challengeParameters()["USERNAME"]!!

        val B = BigInteger(challenge.challengeParameters()["SRP_B"], 16)
        if (B.mod(N) == BigInteger.ZERO) {
            throw SecurityException("SRP error, B cannot be zero")
        }

        val salt = BigInteger(challenge.challengeParameters()["SALT"]!!, 16)
        val key: ByteArray = getPasswordAuthenticationKey(userIdForSRP, password, B, salt)

        val timestamp = Date()
        var hmac: ByteArray? = null
        try {
            val mac = Mac.getInstance("HmacSHA256")
            val keySpec = SecretKeySpec(key, "HmacSHA256")
            mac.init(keySpec)
            mac.update(COGNITO_POOL_ID.split(Regex("_"), 2)[1].toByteArray())
            mac.update(userIdForSRP.toByteArray())
            val secretBlock: ByteArray =
                Base64.getDecoder().decode(challenge.challengeParameters()["SECRET_BLOCK"]!!)
            mac.update(secretBlock)
            val simpleDateFormat = SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy", Locale.US)
            simpleDateFormat.timeZone = SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC")
            val dateString = simpleDateFormat.format(timestamp)
            val dateBytes: ByteArray = dateString.toByteArray()
            hmac = mac.doFinal(dateBytes)
        } catch (e: Exception) {
            println(e)
        }

        val formatTimestamp = SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy", Locale.US)
        formatTimestamp.timeZone = SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC")

        return RespondToAuthChallengeRequest.builder()
            .challengeName(ChallengeNameType.PASSWORD_VERIFIER)
            .clientId(COGNITO_CLIENT_ID)
            .session(challenge.session())
            .challengeResponses(
                mapOf(
                    "PASSWORD_CLAIM_SECRET_BLOCK" to challenge.challengeParameters()["SECRET_BLOCK"]!!,
                    "PASSWORD_CLAIM_SIGNATURE" to String(
                        Base64.getEncoder().encode(hmac),
                        Charsets.UTF_8
                    ),
                    "TIMESTAMP" to formatTimestamp.format(timestamp),
                    "USERNAME" to usernameInternal
                )
            )
            .build()
    }

    private fun getPasswordAuthenticationKey(
        userId: String,
        userPassword: String,
        B: BigInteger,
        salt: BigInteger,
    ): ByteArray {
        // Authenticate the password
        // u = H(A, B)
        val messageDigest: MessageDigest = THREAD_MESSAGE_DIGEST.get()
        messageDigest.reset()
        messageDigest.update(A.toByteArray())
        val u = BigInteger(1, messageDigest.digest(B.toByteArray()))
        if (u == BigInteger.ZERO) {
            throw SecurityException("Hash of A and B cannot be zero")
        }

        // x = H(salt | H(poolName | userId | ":" | password))
        messageDigest.reset()
        messageDigest.update(COGNITO_POOL_ID.split(Regex("_"), 2)[1].toByteArray())
        messageDigest.update(userId.toByteArray())
        messageDigest.update(":".toByteArray())
        val userIdHash = messageDigest.digest(userPassword.toByteArray())
        messageDigest.reset()
        messageDigest.update(salt.toByteArray())
        val x = BigInteger(1, messageDigest.digest(userIdHash))
        val S = B.subtract(k.multiply(g.modPow(x, N)))
            .modPow(a.add(u.multiply(x)), N)
            .mod(N)
        val hkdf = Hkdf.getInstance("HmacSHA256")
        hkdf.init(S.toByteArray(), u.toByteArray())
        return hkdf.deriveKey(DERIVED_KEY_INFO, DERIVED_KEY_SIZE)
    }

    fun initiateUserSrpAuthRequest(username: String): InitiateAuthRequest? =
        InitiateAuthRequest.builder()
            .authFlow(AuthFlowType.USER_SRP_AUTH)
            .clientId(COGNITO_CLIENT_ID)
            .authParameters(
                mutableMapOf(
                    "USERNAME" to username,
                    "SRP_A" to A.toString(16)
                )
            ).build()

    fun refreshToken(refreshToken: String): InitiateAuthRequest =
        InitiateAuthRequest.builder()
            .authFlow(AuthFlowType.REFRESH_TOKEN_AUTH)
            .authParameters(
                mapOf(
                    "REFRESH_TOKEN" to refreshToken
                )
            )
            .clientId(COGNITO_CLIENT_ID)
            .build()
}

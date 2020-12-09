package be.tapped.vrtnu.profile

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.SimpleDateFormat
import java.util.*

public inline class OIDCXSRF(public val token: String)

public inline class XVRTToken(public val token: String)
public inline class AccessToken(public val token: String)
public inline class RefreshToken(public val token: String)
public inline class Expiry(public val date: Long)

public data class TokenWrapper(
    val accessToken: AccessToken,
    val refreshToken: RefreshToken,
    val expiry: Expiry,
)

@Serializable(with = DateSerializer::class)
public class DateWrapper(public val date: Date)

public object DateSerializer : KSerializer<DateWrapper> {
    private val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("java.util.Date", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: DateWrapper) {}

    override fun deserialize(decoder: Decoder): DateWrapper {
        val string = decoder.decodeString()
        return DateWrapper(dateTimeFormatter.parse(string))
    }
}

@Serializable
public data class VRTPlayerToken(val vrtPlayerToken: String, val expirationDate: DateWrapper)

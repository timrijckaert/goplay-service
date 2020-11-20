package be.tapped.vrtnu.authentication

import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.awt.Color
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

inline class OIDCXSRF(val token: String)

inline class XVRTToken(val token: String)
inline class AccessToken(val token: String)
inline class RefreshToken(val token: String)
inline class Expiry(val date: Long)

data class TokenWrapper(
    val accessToken: AccessToken,
    val refreshToken: RefreshToken,
    val expiry: Expiry,
)

@Serializable(with = DateSerializer::class)
class DateWrapper(val date: Date)

object DateSerializer : KSerializer<DateWrapper> {
    private val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("java.util.Date", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: DateWrapper) {}

    override fun deserialize(decoder: Decoder): DateWrapper {
        val string = decoder.decodeString()
        return DateWrapper(dateTimeFormatter.parse(string))
    }
}

@Serializable
data class VRTPlayerToken(val vrtPlayerToken: String, val expirationDate: DateWrapper)

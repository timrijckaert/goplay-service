package be.tapped.vier.content

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.internal.NamedValueDecoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.reflect.KCallable

@Serializable
public data class Images(
    val hero: String,
    val mobile: String,
    val poster: String,
    val teaser: String,
)

@Serializable
public data class HeaderProgram(val title: String, val poster: String)

public object CustomHeaderVideoSerializer : KSerializer<HeaderVideo?> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("HeaderVideo")

    override fun deserialize(decoder: Decoder): HeaderVideo? {
        val input = decoder as JsonDecoder
        val json = input.decodeJsonElement()
        return if (json is JsonArray) {
            null
        } else {
            //delegate to default generator KSerializer
        }
    }

    override fun serialize(encoder: Encoder, value: HeaderVideo?) {}

}

@Serializable(with = CustomHeaderVideoSerializer::class)
public data class HeaderVideo(
    val autoplay: Boolean,
    val cimTag: String,
    val createdDate: Int,
    val description: String,
    val duration: Int,
    val embedCta: JsonPrimitive?,
    val enablePreroll: Boolean,
    val episodeNumber: String,
    val episodeTitle: String?,
    val hasProductPlacement: Boolean,
    val image: String,
    val isProtected: Boolean,
    val isSeekable: Boolean,
    val isStreaming: Boolean,
    val link: String,
    val midrollOffsets: List<JsonObject>,
    val pageInfo: PageInfo,
    val pageUuid: String,
    val parentalRating: String,
    val path: String,
    val program: HeaderProgram,
    val seasonNumber: String,
    val seekableFrom: Int,
    val title: String,
    val type: String,
    val unpublishDate: String,
    val videoUuid: String,
    val whatsonId: String?,
    val needs16PlusLabel: Boolean,
    val badge: String,
)

@Serializable
public data class Header(
    val title: String,
    val video: HeaderVideo? = null,
)

@Serializable
public data class PageInfo(
    val site: String,
    val url: String,
    val nodeId: String,
    val title: String,
    val description: String,
    val type: String,
    val program: String,
    val programId: String,
    val programUuid: String,
    val programKey: String,
    val tags: List<String>,
    val publishDate: Int,
    val unpublishDate: Int,
    val author: String,
    val notificationsScore: Int,
)

@Serializable
public data class Social(
    val facebook: String,
    val hashtag: String,
    val instagram: String,
    val twitter: String,
)

@Serializable
public data class Program(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val label: String,
    val link: String,
    val images: Images,
    val header: Header,
    val pageInfo: PageInfo,
    val playlists: List<JsonObject>,
    val social: Social,
)

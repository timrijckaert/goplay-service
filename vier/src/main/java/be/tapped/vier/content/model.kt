package be.tapped.vier.content

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

@Serializable
public data class Images(
    val hero: String,
    val mobile: String,
    val poster: String,
    val teaser: String,
)

public object CustomHeaderVideoSerializer : KSerializer<HeaderVideo?> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("HeaderVideo")

    override fun deserialize(decoder: Decoder): HeaderVideo? {
        val input = decoder as JsonDecoder
        val json = input.decodeJsonElement()
        return if (json is JsonObject) {
            try {
                val h = decoder.decodeSerializableValue(HeaderVideo2.serializer())
                HeaderVideo(
                    autoplay = h.autoplay,
                    cimTag = h.cimTag,
                    createdDate = h.createdDate,
                    description = h.description,
                    duration = h.duration,
                    embedCta = h.embedCta,
                    enablePreroll = h.enablePreroll,
                    episodeNumber = h.episodeNumber,
                    episodeTitle = h.episodeTitle,
                    hasProductPlacement = h.hasProductPlacement,
                    image = h.image,
                    isProtected = h.isProtected,
                    isSeekable = h.isSeekable,
                    isStreaming = h.isStreaming,
                    link = h.link,
                    midrollOffsets = h.midrollOffsets,
                    pageInfo = h.pageInfo,
                    pageUuid = h.pageUuid,
                    parentalRating = h.parentalRating,
                    path = h.path,
                    seasonNumber = h.seasonNumber,
                    seekableFrom = h.seekableFrom,
                    title = h.title,
                    type = h.type,
                    unpublishDate = h.unpublishDate,
                    videoUuid = h.videoUuid,
                    whatsonId = h.whatsonId,
                    needs16PlusLabel = h.needs16PlusLabel,
                    badge = h.badge,
                )
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    override fun serialize(encoder: Encoder, value: HeaderVideo?) {}

}

// Unable to delegate to generated KSerializer
// This object is a copy of HeaderVideo and it's only purpose is to generate a KSerializer for easy mapping.
@Serializable
public data class HeaderVideo2(
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
    val midrollOffsets: List<Int>,
    val pageInfo: PageInfo,
    val pageUuid: String,
    val parentalRating: String,
    val path: String,
    val seasonNumber: String,
    val seekableFrom: Int,
    val title: String,
    val type: String,
    val unpublishDate: String,
    val videoUuid: String,
    val whatsonId: String?,
    val needs16PlusLabel: Boolean? = null,
    val badge: String? = null,
)

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
    val midrollOffsets: List<Int>,
    val pageInfo: PageInfo,
    val pageUuid: String,
    val parentalRating: String,
    val path: String,
    val seasonNumber: String,
    val seekableFrom: Int,
    val title: String,
    val type: String,
    val unpublishDate: String,
    val videoUuid: String,
    val whatsonId: String?,
    val needs16PlusLabel: Boolean? = null,
    val badge: String? = null,
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
public data class Episode(
    val autoplay: Boolean,
    val badge: String? = null,
    val cimTag: String,
    val createdDate: Int,
    val description: String,
    val duration: Int,
    val embedCta: JsonPrimitive?,
    val enablePreroll: Boolean,
    val episodeNumber: String,
    val episodeTitle: String,
    val hasProductPlacement: Boolean,
    val image: String,
    val isProtected: Boolean,
    val isSeekable: Boolean,
    val isStreaming: Boolean,
    val link: String,
    val midrollOffsets: List<Int>,
    val needs16PlusLabel: Boolean? = null,
    val pageInfo: PageInfo,
    val pageUuid: String,
    val parentalRating: String,
    val path: String,
    val seasonNumber: String,
    val seekableFrom: Int,
    val title: String,
    val type: String,
    val unpublishDate: String,
    val videoUuid: String,
    val whatsonId: String,
)

@Serializable
public data class Playlist(
    val episodes: List<Episode>,
    val id: String,
    val link: String,
    val pageInfo: PageInfo,
    val title: String,
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
    val playlists: List<Playlist>,
    val social: Social,
)

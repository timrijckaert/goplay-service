package be.tapped.vtmgo.epg

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
public enum class LegalIcon {
    PGAL,
    PG12,
    PG16,
    PP,
}

@Serializable
public data class LiveStreamLink(
    val url: String,
    val title: String,
    val iconImageUrl: String,
)

@Serializable
public enum class ImageFormat {
    LANDSCAPE,
    WALLPAPER;
}

@Serializable
public data class Broadcast(
    val uuid: String,
    val playableUuid: String,
    val programUuid: String? = null,
    val channelUuid: String,
    val from: Long,
    val to: Long,
    val title: String,
    val productionCountry: List<JsonElement> = emptyList(),
    val live: Boolean,
    val rerun: Boolean,
    val prime: Boolean,
    val legalIcons: List<LegalIcon>,
    val duration: Int? = null,
    val synopsis: String? = null,
    val playableType: String,
    val genre: String? = null,
    val subGenres: List<JsonElement>,
    val tip: Boolean,
    val rating: JsonElement? = null,
    val imageUrl: String? = null,
    val imageFormat: ImageFormat? = null,
    val videoOnDemandLinks: List<JsonElement>,
    val entitlements: List<JsonElement>,
    val fromIso: String,
    val toIso: String,
)

@Serializable
public data class Channel(
    val name: String,
    val seoKey: String,
    val uuid: String,
    val channelLogoUrl: String,
    val broadcasts: List<Broadcast>,
    val liveStreamLinks: List<LiveStreamLink>,
    val order: Int,
)

@Serializable
public data class Epg(
    val from: Long,
    val to: Long,
    val timestamp: Long,
    val channels: List<Channel>,
)

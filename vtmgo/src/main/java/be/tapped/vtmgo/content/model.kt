package be.tapped.vtmgo.content

import com.sun.org.apache.xpath.internal.operations.Bool
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import java.awt.Image

enum class TargetType {
    MOVIE,
    PROGRAM,
    EPISODE,
    EXTERNAL_URL;
}

@Serializable
data class TargetResponse(
    private val type: TargetType,
    private val id: String? = null,
    private val url: String? = null,
    private val name: String? = null,
    private val programId: String? = null,
) {
    sealed class Target {
        data class Movie(val id: String, val name: String) : Target()
        data class Program(val id: String, val name: String) : Target()
        data class Episode(val id: String, val programId: String) : Target()
        data class External(val url: String) : Target()
    }

    val asTarget: Target
        get() {
            return when (type) {
                TargetType.MOVIE -> Target.Movie(id!!, name!!)
                TargetType.PROGRAM -> Target.Program(id!!, name!!)
                TargetType.EPISODE -> Target.Episode(id!!, programId!!)
                TargetType.EXTERNAL_URL -> Target.External(url!!)
            }
        }
}

@Serializable
data class PagedTeaserContent(
    val title: String,
    val target: TargetResponse,
    val imageUrl: String,
    val geoBlocked: Boolean,
    val blockedFor: String? = null,
)

@Serializable
data class CategoryResponse(@SerialName("catalogFilters") val categories: List<Category>)

@Serializable
data class Category(val id: String, val title: String, val active: Boolean)

@Serializable
data class Broadcast(val name: String, val startsAt: String, val endsAt: String)

@Serializable
data class LiveChannel(
    val name: String,
    val broadcasts: List<Broadcast>,
    val channelId: String,
    val channelLogoUrl: String,
    val channelPosterUrl: String,
    val seoKey: String,
)

@Serializable
enum class Overlay {
    TITLE,
    BUTTONS
}

@Serializable
data class CarouselTeaser(
    val tagline: String? = null,
    val bannerAltText: String? = null,
    val largeImageUrl: String,
    val mediumImageUrl: String,
    val mobileImageUrl: String,
    val mobileCompressedImageUrl: String,
    val smartTvImageUrl: String,
    val visualOnly: Boolean,
    val addedToMyList: Boolean,
    val overlay: List<Overlay>,
    val title: String,
    val target: TargetResponse,
)

@Serializable
data class DefaultSwimlaneTeaser(
    val title: String,
    val target: TargetResponse,
    val imageUrl: String,
    val geoBlocked: Boolean,
    val blockedFor: String? = null,
)

@Serializable
data class ContinueWatchingTeaser(
    val title: String,
    val label: String? = null,
    val target: TargetResponse,
    val imageUrl: String,
    val userProgressPercentage: Int,
    val playerPositionSeconds: Int,
    val remainingDaysAvailable: Long,
    val geoBlocked: Boolean,
    val blockedFor: String? = null,
)

@Serializable
data class MyListTeaser(
    val title: String,
    val target: TargetResponse,
    val imageUrl: String,
    val geoBlocked: Boolean,
    val blockedFor: String? = null,
)

@Serializable
data class MarketingTeaser(
    val tagline: String? = null,
    val bannerAltText: String? = null,
    val largeImageUrl: String,
    val mediumImageUrl: String,
    val mobileImageUrl: String,
    val mobileCompressedImageUrl: String,
    val smartTvImageUrl: String,
    val visualOnly: Boolean,
    val addedToMyList: Boolean,
    val overlay: List<Overlay>,
    val title: String,
    val target: TargetResponse,
)

@Serializable
data class Metadata(
    val provider: String,
    val requestId: String,
    val routingGroup: String,
    val abGroup: String,
)

@Serializable
sealed class StoreFront {
    abstract val rowType: String
    abstract val id: String
    abstract val metaData: Metadata
    abstract val hasDetail: Boolean

    @Serializable
    @SerialName("CAROUSEL")
    data class CarouselStoreFront(
        override val id: String,
        val teasers: List<CarouselTeaser>,
        override val metaData: Metadata,
        override val hasDetail: Boolean,
        override val rowType: String,
    ) : StoreFront()

    @Serializable
    @SerialName("SWIMLANE_DEFAULT")
    data class DefaultSwimlaneStoreFront(
        override val id: String,
        override val metaData: Metadata,
        val title: String,
        val logoUrl: String? = null,
        val teasers: List<DefaultSwimlaneTeaser>,
        override val hasDetail: Boolean,
        override val rowType: String,
    ) : StoreFront()

    @Serializable
    @SerialName("CONTINUE_WATCHING")
    data class ContinueWatchingStoreFront(
        override val id: String,
        override val metaData: Metadata,
        val title: String,
        val logoUrl: String? = null,
        val teasers: List<ContinueWatchingTeaser>,
        override val hasDetail: Boolean,
        override val rowType: String,
    ) : StoreFront()

    @Serializable
    @SerialName("MY_LIST")
    data class MyListStoreFront(
        override val id: String,
        override val metaData: Metadata,
        val title: String,
        val logoUrl: String? = null,
        val teasers: List<MyListTeaser>,
        override val hasDetail: Boolean,
        override val rowType: String,
    ) : StoreFront()

    @Serializable
    @SerialName("MARKETING_BLOCK")
    data class MarketingStoreFront(
        override val id: String,
        val teaser: MarketingTeaser,
        override val metaData: Metadata,
        override val hasDetail: Boolean,
        override val rowType: String,
    ) : StoreFront()

    @Serializable
    @SerialName("PROFILE_SWITCHER")
    data class ProfileSwitcherStoreFront(
        override val id: String,
        override val metaData: Metadata,
        val title: String,
        val profiles: List<JsonObject>,
        override val hasDetail: Boolean,
        override val rowType: String,
    ) : StoreFront()
}

@Serializable
enum class SearchResultType {
    @SerialName("related")
    RELATED,

    @SerialName("exact")
    EXACT,

    @SerialName("fallback")
    FALLBACK;
}

@Serializable
data class SearchResultResponse(
    val title: String,
    val type: SearchResultType,
    val teasers: List<PagedTeaserContent>,
)

@Serializable
data class Anvato(val video: String, val mcp: String, val accessKey: String, val token: String)

@Serializable
data class Stream(
    val type: String,
    val anvato: Anvato,
)

@Serializable
data class Channel(val id: String, val title: String)

@Serializable
data class Program(val id: String, val title: String)

@Serializable
data class StreamBroadcast(val id: String, val technicalFromMs: Long, val technicalToMs: Long)

@Serializable
data class Synopsis(val xs: String, val s: String, val m: String)

@Serializable
data class Season(val order: Int)

@Serializable
data class Episode(val order: Int, val season: Season)

@Serializable
data class PosterImage(val height: Int, val url: String)

@Serializable
data class StreamMetadata(
    val id: String,
    val geoBlocked: Boolean,
    val availability: Int? = null,
    val assetType: String,
    val title: String,
    val channel: Channel,
    val videoType: String,
    val program: Program? = null,
    val broadcast: StreamBroadcast,
    val synopsis: Synopsis? = null,
    val episode: Episode? = null,
    val legalTags: List<String>,
    val posterImages: List<PosterImage>,
)

@Serializable
data class Freewheel(
    val serverSide: Boolean,
    val serverUrl: String,
    val profileId: String,
    val networkId: String,
    val assetId: String? = null,
)

@Serializable
data class Ads(
    val provider: String,
    val freewheel: Freewheel,
)

@Serializable
data class CIM(
    val identifier: String,
    val materialId: String,
    val sourceType: String,
    val contentType: String,
    val name: String,
    val linkTv: String,
    val channel: String,
)

@Serializable
data class Analytics(val cim: CIM)

@Serializable
data class StreamResponse(
    val streamType: String,
    val streams: List<Stream>,
    val analytics: Analytics,
    val ads: Ads,
    val metadata: StreamMetadata,
)

@Serializable
data class PublishedUrl(
    @SerialName("embed_url")
    val embedUrl: String,
    val format: String,
    val kbps: Int,
    @SerialName("cdn_name")
    val cdnName: String,
    @SerialName("format_name")
    val formatName: String,
    val width: Int,
    val height: Int,
    @SerialName("cdn_id")
    val cdnId: Int,
    val protocol: String,
    @SerialName("license_url")
    val licenseUrl: String,
    @SerialName("backup_url")
    val backupUrl: String,
    @SerialName("backup_license_url")
    val backupLicenseUrl: String,
)

@Serializable
data class Rstv(val type: String, val window: Int)

@Serializable
data class AnvatoVideoStreamResponse(
    @SerialName("upload_id")
    val uploadId: String,
    val mcp_id: String,
    @SerialName("owner_id")
    val ownerId: String,
    @SerialName("def_title")
    val defTitle: String,
    @SerialName("def_callsign")
    val defCallSign: String,
    val dvr: String,
    @SerialName("video_type")
    val videoType: String,
    @SerialName("src_image_url")
    val srcImageUrl: String,
    @SerialName("src_logo_url")
    val srcLogoUrl: String,
    @SerialName("published_urls")
    val publishedUrls: List<PublishedUrl>,
    @SerialName("logo_settings")
    val logoSettings: List<JsonElement> = emptyList(),
    @SerialName("source_channel_id")
    val sourceChannelId: String,
    @SerialName("requested_id")
    val requestedId: String,
    @SerialName("redirected_id")
    val redirectedId: String,
    @SerialName("primary_id")
    val primaryId: String,
    val rstv: Rstv,
    @SerialName("backup_id")
    val backupId: String,
    @SerialName("access_rules")
    val accessRules: List<JsonElement> = emptyList(),
    val generated: String,
)

inline class MPDUrl(val url: String)
inline class LicenseUrl(val url: String)

data class AnvatoStreamWrapper(
    val rawMdpUrl: MPDUrl,
    val mdpUrl: MPDUrl,
    val rawBackUpMdpUrl: MPDUrl,
    val backUpMdpUrl: MPDUrl,
    val licenseUrl: LicenseUrl,
    val backUpLicenseUrl: LicenseUrl,
)

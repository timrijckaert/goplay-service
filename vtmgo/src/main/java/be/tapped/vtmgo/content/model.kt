package be.tapped.vtmgo.content

import be.tapped.vtmgo.epg.LegalIcon
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

internal object DrmSerializer : JsonTransformingSerializer<Drm>(Drm.serializer()) {

    override fun transformDeserialize(element: JsonElement): JsonElement {
        val jsonObj = (element as JsonObject)
        val drmObj = (jsonObj["com.apple.fps.1_0"] ?: jsonObj["com.widevine.alpha"]
        ?: jsonObj["com.microsoft.playready"]) as JsonObject
        return buildJsonObject {
            put("provider", drmObj["provider"]?.jsonPrimitive?.content)
            put("licenseUrl", drmObj["licenseUrl"]?.jsonPrimitive?.content)
            put("certificate", drmObj["certificate"]?.jsonPrimitive?.content)
        }
    }
}

public enum class TargetType {
    MOVIE,
    PROGRAM,
    EPISODE,
    EXTERNAL_URL;
}

@Serializable
public data class TargetResponse(
        private val type: TargetType,
        private val id: String? = null,
        private val url: String? = null,
        private val name: String? = null,
        private val programId: String? = null,
) {
    public sealed class Target {
        public data class Movie(val id: String) : Target()
        public data class Program(val id: String) : Target()
        public data class Episode(val id: String) : Target()
        public data class External(val url: String) : Target()
    }

    val asTarget: Target
        get() = when (type) {
            TargetType.MOVIE -> Target.Movie(id!!)
            TargetType.PROGRAM -> Target.Program(id!!)
            TargetType.EPISODE -> Target.Episode(id!!)
            TargetType.EXTERNAL_URL -> Target.External(url!!)
        }
}

@Serializable
public data class PagedTeaserContent(
        val title: String,
        val target: TargetResponse,
        val imageUrl: String? = null,
        val geoBlocked: Boolean,
        val blockedFor: String? = null,
)

@Serializable
public data class CategoryResponse(@SerialName("catalogFilters") val categories: List<Category>)

@Serializable
public data class Category(val id: String, val title: String, val active: Boolean)

@Serializable
public data class Broadcast(val name: String, val startsAt: String, val endsAt: String)

@Serializable
public data class LiveChannel(
        val name: String,
        val broadcasts: List<Broadcast>,
        val channelId: String,
        val channelLogoUrl: String,
        val channelPosterUrl: String,
        val seoKey: String,
)

@Serializable
public enum class Overlay {
    TITLE,
    BUTTONS
}

@Serializable
public data class CarouselTeaser(
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
public data class DefaultSwimlaneTeaser(
        val title: String,
        val target: TargetResponse,
        val imageUrl: String? = null,
        val geoBlocked: Boolean,
        val blockedFor: String? = null,
)

@Serializable
public data class ContinueWatchingTeaser(
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
public data class MyListTeaser(
        val title: String,
        val target: TargetResponse,
        val imageUrl: String,
        val geoBlocked: Boolean,
        val blockedFor: String? = null,
)

@Serializable
public data class MarketingTeaser(
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
public data class Metadata(
        val provider: String,
        val requestId: String,
        val routingGroup: String,
        val abGroup: String,
)

@Serializable
public sealed class StoreFront {
    public abstract val rowType: String
    public abstract val id: String
    public abstract val metaData: Metadata
    public abstract val hasDetail: Boolean

    @Serializable
    @SerialName("CAROUSEL")
    public data class CarouselStoreFront(
            override val id: String,
            val teasers: List<CarouselTeaser>,
            override val metaData: Metadata,
            override val hasDetail: Boolean,
            override val rowType: String,
    ) : StoreFront()

    @Serializable
    @SerialName("SWIMLANE_DEFAULT")
    public data class DefaultSwimlaneStoreFront(
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
    public data class ContinueWatchingStoreFront(
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
    public data class MyListStoreFront(
            override val id: String,
            override val metaData: Metadata,
            val title: String,
            val logoUrl: String? = null,
            val teasers: List<MyListTeaser>,
            override val hasDetail: Boolean,
            override val rowType: String,
            val branding: String? = null
    ) : StoreFront()

    @Serializable
    @SerialName("MARKETING_BLOCK")
    public data class MarketingStoreFront(
            override val id: String,
            val teaser: MarketingTeaser,
            override val metaData: Metadata,
            override val hasDetail: Boolean,
            override val rowType: String,
    ) : StoreFront()

    @Serializable
    @SerialName("PROFILE_SWITCHER")
    public data class ProfileSwitcherStoreFront(
            override val id: String,
            override val metaData: Metadata,
            val title: String,
            val profiles: List<JsonObject>,
            override val hasDetail: Boolean,
            override val rowType: String,
    ) : StoreFront()
}

@Serializable
public enum class SearchResultType {
    @SerialName("related")
    RELATED,

    @SerialName("exact")
    EXACT,

    @SerialName("fallback")
    FALLBACK;
}

@Serializable
public data class SearchResultResponse(
        val title: String,
        val type: SearchResultType,
        val teasers: List<PagedTeaserContent>,
)

public enum class StreamType {
    ANVATO_VOD,
    ANVATO_LIVE,
    DASH,
    HLS
}

@Serializable
public data class Anvato(val video: String, val mcp: String, val accessKey: String, val token: String)

@Serializable
public data class Drm(val provider: String, val licenseUrl: String, val certificate: String? = null)

@Serializable
public data class Stream(
        val type: String,
        val anvato: Anvato? = null,
        val url: String? = null,
        @Serializable(with = DrmSerializer::class) val drm: Drm? = null,
)

@Serializable
public data class AnvatoChannel(val id: String, val title: String)

@Serializable
public data class AnvatoProgram(val id: String, val title: String)

@Serializable
public data class StreamBroadcast(val id: String, val technicalFromMs: Long, val technicalToMs: Long)

@Serializable
public data class AnvatoSynopsis(val xs: String? = null, val s: String? = null, val m: String? = null)

@Serializable
public data class AnvatoSeason(val order: Int)

@Serializable
public data class AnvatoEpisode(val order: Int, val season: AnvatoSeason)

@Serializable
public data class PosterImage(val height: Int, val url: String)

@Serializable
public data class StreamMetadata(
        val id: String,
        val geoBlocked: Boolean,
        val availability: Int? = null,
        val assetType: String,
        val title: String,
        val creator: String? = null,
        val channel: AnvatoChannel? = null,
        val videoType: String,
        val program: AnvatoProgram? = null,
        val broadcast: StreamBroadcast? = null,
        val synopsis: AnvatoSynopsis? = null,
        val episode: AnvatoEpisode? = null,
        val legalTags: List<String>? = null,
        val createdAt: String? = null,
        val posterImages: List<PosterImage>? = null,
        val markers: List<Marker>? = null,
) {
    @Serializable
    public data class Marker(val type: String, val start: Int, val end: Int, val countdown: Int? = null, val skipTo: Int? = null)
}

@Serializable
public data class Spott(
        val id: String,
        val trackedTags: Boolean,
        val showInteractiveInstructions: Boolean,
)

@Serializable
public data class Freewheel(
        val assetId: String? = null,
        val serverSide: Boolean? = null,
        val clientSide: Boolean? = null,
        val serverUrl: String,
        val profileId: String,
        val networkId: String,
)

@Serializable
public data class Ads(
        val provider: String,
        val freewheel: Freewheel,
        val spott: Spott? = null,
)

@Serializable
public data class CIM(
        val identifier: String,
        val materialId: String,
        val sourceType: String,
        val contentType: String,
        val name: String,
        val linkTv: String,
        val channel: String? = null,
)

@Serializable
public data class Analytics(val cim: CIM)

@Serializable
public data class Subtitle(val language: String, val url: String)

@Serializable
public data class StreamResponse(
        val streamType: String,
        val streams: List<Stream>,
        val analytics: Analytics,
        val ads: Ads,
        val subtitles: List<Subtitle> = emptyList(),
        val duration: Int? = null,
        val metadata: StreamMetadata,
)

public data class AnvatoPublishedUrl(
        val embedUrl: String,
        val licenseUrl: String,
        val backupUrl: String?,
        val backupLicenseUrl: String?,
)

public inline class MPDUrl(public val url: String)
public inline class HlsUrl(public val url: String)
public inline class M3U8Url(public val url: String)

public inline class LicenseUrl(public val url: String)
public inline class HlsCertificate(public val certificate: String)

public sealed class AnvatoStream {
    public abstract val mdpUrl: MPDUrl
    public abstract val licenseUrl: LicenseUrl

    public data class Live(
            val rawMdpUrl: MPDUrl,
            override val mdpUrl: MPDUrl,
            val rawBackUpMdpUrl: MPDUrl,
            val backUpMdpUrl: MPDUrl,
            override val licenseUrl: LicenseUrl,
            val backUpLicenseUrl: LicenseUrl,
    ) : AnvatoStream()

    public data class Episode(override val mdpUrl: MPDUrl, override val licenseUrl: LicenseUrl, val subtitle: List<Subtitle>) : AnvatoStream()
}

@Serializable
public data class Episode(
        val id: String,
        val name: String,
        val description: String? = null,
        val index: Int,
        val bigPhotoUrl: String,
        val mediumPhotoUrl: String,
        val smallPhotoUrl: String,
        val userProgressPercentage: Int,
        val playerPositionSeconds: Int,
        val durationSeconds: Int? = null,
        val remainingDaysAvailable: Int,
        val broadcastTimestamp: String? = null,
        val doneWatching: Boolean,
        val geoBlocked: Boolean,
        val ageBlocked: Boolean,
        val blockedFor: String? = null,
        val downloadAllowed: Boolean,
) {
    init {
        require(id.isNotEmpty()) { "id is not empty" }
        require(name.isNotEmpty()) { "name is not empty" }
        require(bigPhotoUrl.isNotEmpty()) { "bigPhotoUrl is not empty" }
        require(mediumPhotoUrl.isNotEmpty()) { "mediumPhotoUrl is not empty" }
        require(smallPhotoUrl.isNotEmpty()) { "smallPhotoUrl is not empty" }
    }
}

@Serializable
public data class Season(
        val episodes: List<Episode>,
        val trailers: List<JsonElement> = emptyList(),
        val catchUpEpisode: JsonElement? = null,
        val index: Int,
        val active: Boolean,
)

@Serializable
public data class Program(
        val id: String,
        val name: String,
        val description: String? = null,
        val bigPhotoUrl: String? = null,
        val mediumPhotoUrl: String? = null,
        val smallPhotoUrl: String? = null,
        val teaserImageUrl: String? = null,
        val activeSeasonIndex: Int,
        val activeEpisodeId: String,
        val watching: Boolean,
        val channelLogoUrl: String? = null,
        val seasons: List<Season>,
        val geoBlocked: Boolean,
        val blockedFor: String? = null,
        val kidsContent: Boolean,
        val legalIcons: List<LegalIcon>,
        val addedToMyList: Boolean,
) {
    init {
        require(id.isNotEmpty()) { "id is not empty" }
        require(name.isNotEmpty()) { "name is not empty" }
        require(activeEpisodeId.isNotEmpty()) { "activeEpisodeId is not empty" }
        require(seasons.isNotEmpty()) { "seasons is not empty" }
    }
}

package be.tapped.vtmgo.content

import be.tapped.vtmgo.epg.LegalIcon
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

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
        get() = when (type) {
            TargetType.MOVIE -> Target.Movie(id!!, name!!)
            TargetType.PROGRAM -> Target.Program(id!!, name!!)
            TargetType.EPISODE -> Target.Episode(id!!, programId!!)
            TargetType.EXTERNAL_URL -> Target.External(url!!)
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
data class AnvatoChannel(val id: String, val title: String)

@Serializable
data class AnvatoProgram(val id: String, val title: String)

@Serializable
data class StreamBroadcast(val id: String, val technicalFromMs: Long, val technicalToMs: Long)

@Serializable
data class AnvatoSynopsis(val xs: String? = null, val s: String? = null, val m: String? = null)

@Serializable
data class AnvatoSeason(val order: Int)

@Serializable
data class AnvatoEpisode(val order: Int, val season: AnvatoSeason)

@Serializable
data class PosterImage(val height: Int, val url: String)

@Serializable
data class StreamMetadata(
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
    val legalTags: List<String>,
    val createdAt: String? = null,
    val posterImages: List<PosterImage>,
)

@Serializable
data class Freewheel(
    val assetId: String? = null,
    val serverSide: Boolean,
    val serverUrl: String,
    val profileId: String,
    val networkId: String,
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
    val channel: String? = null,
)

@Serializable
data class Analytics(val cim: CIM)

@Serializable
data class Subtitle(val language: String, val url: String)

@Serializable
data class StreamResponse(
    val streamType: String,
    val streams: List<Stream>,
    val analytics: Analytics,
    val ads: Ads,
    val subtitles: List<Subtitle>? = null,
    val duration: Int? = null,
    val metadata: StreamMetadata,
)

data class AnvatoPublishedUrl(
    val embedUrl: String,
    val licenseUrl: String,
    val backupUrl: String?,
    val backupLicenseUrl: String?,
)

inline class MPDUrl(val url: String)
inline class LicenseUrl(val url: String)

data class AnvatoStreamWrapper(
    val rawMdpUrl: MPDUrl,
    val mdpUrl: MPDUrl,
    val rawBackUpMdpUrl: MPDUrl? = null,
    val backUpMdpUrl: MPDUrl? = null,
    val licenseUrl: LicenseUrl,
    val backUpLicenseUrl: LicenseUrl? = null,
)

@Serializable
data class Episode(
    val id: String,
    val name: String,
    val description: String,
    val index: Int,
    val bigPhotoUrl: String,
    val mediumPhotoUrl: String,
    val smallPhotoUrl: String,
    val userProgressPercentage: Int,
    val playerPositionSeconds: Int,
    val durationSeconds: Int,
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
        require(description.isNotEmpty()) { "description is not empty" }
        require(bigPhotoUrl.isNotEmpty()) { "bigPhotoUrl is not empty" }
        require(mediumPhotoUrl.isNotEmpty()) { "mediumPhotoUrl is not empty" }
        require(smallPhotoUrl.isNotEmpty()) { "smallPhotoUrl is not empty" }
    }
}

@Serializable
data class Season(
    val episodes: List<Episode>,
    val trailers: List<JsonElement> = emptyList(),
    val catchUpEpisode: JsonElement? = null,
    val index: Int,
    val active: Boolean,
)

@Serializable
data class Program(
    val id: String,
    val name: String,
    val description: String,
    val bigPhotoUrl: String,
    val mediumPhotoUrl: String,
    val smallPhotoUrl: String,
    val teaserImageUrl: String,
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
        require(description.isNotEmpty()) { "description is not empty" }
        require(bigPhotoUrl.isNotEmpty()) { "bigPhotoUrl is not empty" }
        require(mediumPhotoUrl.isNotEmpty()) { "mediumPhotoUrl is not empty" }
        require(smallPhotoUrl.isNotEmpty()) { "smallPhotoUrl is not empty" }
        require(teaserImageUrl.isNotEmpty()) { "teaserImageUrl is not empty" }
        require(activeEpisodeId.isNotEmpty()) { "activeEpisodeId is not empty" }
        require(seasons.isNotEmpty()) { "seasons is not empty" }
    }
}

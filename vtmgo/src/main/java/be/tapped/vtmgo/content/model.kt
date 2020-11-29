package be.tapped.vtmgo.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
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
    val teasers: List<PagedTeaserContent>
)

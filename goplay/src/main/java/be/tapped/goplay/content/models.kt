package be.tapped.goplay.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@JvmInline
public value class EpisodeUuid(public val id: String)

@JvmInline
public value class VideoUuid(public val id: String)

@JvmInline
public value class M3U8Stream(public val url: String)

/**
 * @param label: Most of time this is an empty string. Sometimes it contains values such as "Volledig seizoen", "nieuw", "Volledige reeks"
 */
@Serializable
public data class Program(
    val id: Id,
    val link: Link,
    val title: String,
    val label: String,
    val pageInfo: PageInfo,
    val images: Images,
) {

    @Serializable
    @JvmInline
    public value class Id(public val id: String)

    /**
     * @param link: Prefixed by '/'
     */
    @Serializable
    @JvmInline
    public value class Link(public val link: String)

    @Serializable
    public data class Images(val poster: String, val teaser: String)

    @Serializable
    public data class PageInfo(val brand: Brand) {
        public enum class Brand {
            Play4,
            Play5,
            Play6,
            Play7,
            GoPlay;
        }
    }
}

//<editor-fold desc="Search">
@Serializable
public data class SearchHit(
    @SerialName("_index") val index: String,
    @SerialName("_type") val type: String,
    @SerialName("_id") val id: String,
    @SerialName("_score") val score: Double,
    @SerialName("_source") val source: Source,
    val highlight: Highlight? = null,
) {
    @Serializable
    public data class Source(
        val id: String,
        val type: String,
        val bundle: Bundle,
        private val url: String,
        val language: String,
        val title: String,
        val site: String,
        val intro: String,
        val created: Int,
        val changed: Int,
        val body: List<String>,
        val terms: List<String>,
        val suggest: String,
        val program: String,
        val img: String,
        val videos: Int? = null,
        val duration: Int? = null,
    ) {

        @Serializable
        public enum class Bundle {
            @SerialName("program")
            PROGRAM,

            @SerialName("video")
            VIDEO,

            @SerialName("stub")
            STUB,

            @SerialName("article")
            ARTICLE,

            @SerialName("playlist")
            PLAYLIST
        }
    }

    @Serializable
    public data class Highlight(
        val title: List<String> = emptyList(),
        val intro: List<String> = emptyList(),
        val body: List<String> = emptyList(),
    )
}
//</editor-fold>

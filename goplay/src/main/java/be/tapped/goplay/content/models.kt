package be.tapped.goplay.content

import kotlinx.serialization.Serializable

// TODO discuss the duplication here

//<editor-fold desc="Overview">
/**
 * @param label: Most of time this is an empty string. Sometimes it contains values such as "Volledig seizoen", "nieuw", "Volledige reeks"
 */
@Serializable
public data class ProgramOverview(
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
//</editor-fold>

//<editor-fold desc="Detail">
/**
 * @param label: Most of time this is an empty string. Sometimes it contains values such as "Volledig seizoen", "nieuw", "Volledige reeks"
 */
@Serializable
public data class ProgramDetail(
    val id: Id,
    val link: Link,
    val title: String,
    val label: String,
    val description: String,
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
    public data class Images(
        val hero: String,
        val mobile: String,
        val poster: String,
        val teaser: String,
        val moviePoster: String,
    )

    @Serializable
    public data class PageInfo(val brand: Brand, val publishDate: String) {
        public enum class Brand {
            Play4,
            Play5,
            Play6,
            Play7,
            GoPlay;
        }
    }
}
//</editor-fold>

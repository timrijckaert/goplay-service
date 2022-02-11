package be.tapped.goplay.content

import kotlinx.serialization.Serializable

/**
 * @param label: Most of time this is an empty string. Sometimes it contains values such as "Volledig seizoen", "nieuw", "Volledige reeks"
 */
@Serializable
public data class Program(
    val id: String,
    val link: String,
    val title: String,
    val label: String,
    val pageInfo: PageInfo,
    val images: Images,
) {
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

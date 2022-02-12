package be.tapped.goplay.content

import kotlinx.serialization.Serializable

public sealed interface Program {
    public val id: Id

    /**
     * Prefixed by '/'
     */
    public val link: Link
    public val title: String

    /**
     * Most of time this is an empty string. Sometimes it contains values such as "Volledig seizoen", "nieuw", "Volledige reeks"
     */
    public val label: String
    public val pageInfo: PageInfo
    public val images: Images

    @Serializable
    @JvmInline
    public value class Id(public val id: String)

    @Serializable
    @JvmInline
    public value class Link(public val link: String)

    public sealed interface Images {
        public val poster: String
        public val teaser: String
    }

    public sealed interface PageInfo {
        public val brand: Brand

        public enum class Brand {
            Play4,
            Play5,
            Play6,
            Play7,
            GoPlay;
        }
    }

    @Serializable
    public data class Overview(
        override val id: Id,
        override val link: Link,
        override val title: String,
        override val label: String,
        override val pageInfo: PageInfo,
        override val images: Images,
    ) : Program {
        @Serializable
        public data class PageInfo(override val brand: Program.PageInfo.Brand) : Program.PageInfo

        @Serializable
        public data class Images(override val poster: String, override val teaser: String) : Program.Images
    }

    @Serializable
    public data class Detail(
        override val id: Id,
        override val link: Link,
        override val title: String,
        override val label: String,
        val description: String,
        override val pageInfo: PageInfo,
        override val images: Images,
        val playlists: List<Playlist>
    ) : Program {

        @Serializable
        public data class PageInfo(
            override val brand: Program.PageInfo.Brand,
            val publishDate: String,
        ) : Program.PageInfo

        @Serializable
        public data class Images(
            val hero: String,
            val mobile: String,
            override val poster: String,
            override val teaser: String,
            val moviePoster: String,
        ) : Program.Images

        @Serializable
        public data class Playlist(
            val id: PlaylistId,
            val link: String,
            /**
             * For example: "Seizoen 1", "Seizoen 2", ...
             */
            val title: String,
            val description: String? = null,
            val episodes: List<Episode>,
        ) {
            @JvmInline
            @Serializable
            public value class PlaylistId(public val id: String)

            @Serializable
            public data class Episode(
                val videoUuid: VideoUuid,
                val pageInfo: PageInfo? = null,
                val link: String,
                /**
                 * Example "Culinaire Speurneuzen - S2 - Aflevering 1"
                 */
                val title: String,
                val description: String,
                val image: String,
                val duration: Long,
                val seasonNumber: String,
                // TODO Not every episode has an 'episodeNumber' ?!
                // val episodeNumber: Int,
                val createdDate: String,
            ) {
                @JvmInline
                @Serializable
                public value class VideoUuid(public val videoUuid: String)

                @Serializable
                public data class PageInfo(val nodeId: String, val site: String? = null)
            }
        }
    }
}



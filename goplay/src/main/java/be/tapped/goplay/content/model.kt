package be.tapped.goplay.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.*

public inline class EpisodeUuid(public val id: String)

@Serializable
public inline class VideoUuid(public val id: String)
public inline class M3U8Stream(public val url: String)

public object HeaderVideoSerializer : JsonTransformingSerializer<List<Program.HeaderVideo>>(ListSerializer(Program.HeaderVideo.serializer())) {
    override fun transformDeserialize(element: JsonElement): JsonElement = if (element is JsonArray) {
        element
    } else {
        JsonArray(listOf(element))
    }
}

public object ProgramSerializer : JsonTransformingSerializer<Program.Playlist.Episode.Program>(Program.Playlist.Episode.Program.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement = if (element is JsonObject) {
        element
    } else {
        JsonNull
    }
}

@Serializable
public data class PageInfo(
        val site: String? = null,
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
) {
    @Serializable
    public data class Social(
            val facebook: String,
            val hashtag: String,
            val instagram: String,
            val twitter: String,
    )

    @Serializable
    public data class Images(
            val hero: String,
            val mobile: String,
            val poster: String,
            val teaser: String,
    )

    @Serializable
    public data class HeaderVideo(
            val autoplay: Boolean,
            val cimTag: String,
            val createdDate: Int,
            val description: String,
            val duration: Int,
            val embedCta: EmbedCta?,
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
    public data class EmbedCta(
            val label: String,
            val title: String,
            val description: String,
            val link: Link,
            val image: String,
    ) {
        @Serializable
        public data class Link(
                val url: String,
                val text: String,
                val external: Boolean,
        )
    }

    @Serializable
    public data class Header(
            val title: String,
            @Serializable(with = HeaderVideoSerializer::class) val video: List<HeaderVideo> = emptyList(),
    )

    @Serializable
    public data class Playlist(
            val episodes: List<Episode>,
            val id: String,
            val link: String,
            val pageInfo: PageInfo,
            val title: String,
    ) {
        @Serializable
        public data class Episode(
                val autoplay: Boolean,
                val badge: String? = null,
                val cimTag: String,
                val createdDate: Int,
                val description: String,
                val duration: Int,
                val embedCta: EmbedCta?,
                val enablePreroll: Boolean,
                val episodeNumber: String,
                val episodeTitle: String? = null,
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
                val videoUuid: VideoUuid,
                val whatsonId: String? = null,
                // https://github.com/Kotlin/kotlinx.serialization/issues/1253
                // @Serializable(with = ProgramSerializer::class)
                // val program: Program? = null,
        ) {

            @Serializable
            public data class Program(val title: String, val poster: String)
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

        public sealed class SearchKey {
            public data class Program(internal val url: String) : SearchKey()
            public data class EpisodeByNodeId(internal val nodeId: String, internal val url: String) : SearchKey()
            public object Invalid : SearchKey()
        }

        val searchKey: SearchKey
            get() = when (bundle) {
                Bundle.PROGRAM -> SearchKey.Program(url)
                Bundle.VIDEO -> SearchKey.EpisodeByNodeId(id, url)
                Bundle.STUB,
                Bundle.ARTICLE,
                Bundle.PLAYLIST,
                -> SearchKey.Invalid
            }

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

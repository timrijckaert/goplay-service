package be.tapped.vrtnu.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pages(
    val total: Int,
    val current: Int,
    val size: Int,
)

@Serializable
data class Meta(
    @SerialName("total_results")
    val totalResults: Int,
    val pages: Pages,
)

@Serializable
data class Bucket(
    val key: String,
    @SerialName("doc_count")
    val docCount: Int,
)

@Serializable
data class Facet(
    val name: String,
    val buckets: List<Bucket>,
)

@Serializable
data class FacetWrapper(val facets: List<Facet>)

@Serializable
data class ElasticSearchResult<T>(
    val meta: Meta,
    val results: List<T>,
    val facets: FacetWrapper,
)

@Serializable
data class AZProgram(
    val title: String,
    val type: String,
    @SerialName("episode_count")
    val episodeCount: Int,
    val score: Double,
    val programUrl: String,
    val targetUrl: String,
    val programName: String,
    val thumbnail: String,
    val alternativeImage: String,
    val brands: List<String>,
    val description: String,
)

@Serializable
data class Image(
    val src: String,
    val srcUriTemplate: String,
    val focalPoint: String,
    val id: String,
    val hiddenInApp: Boolean,
    @SerialName(":type")
    val type: String,
    val alt: String? = null,
)

@Serializable
data class Reference(
    val link: String,
    val modelUri: String,
    val referenceType: String,
    val permalink: String? = null,
)

@Serializable
data class Category(
    val imageStoreUrl: String,
    val name: String,
    val permalink: String? = null,
    val modelUri: String,
    val title: String,
    val link: String,
    val thumbnailUrl: String,
    val image: Image,
    val reference: Reference,
    val description: String? = null,
    @SerialName(":type")
    val type: String,
)

@Serializable
data class DisplayOptions(
    val hideNumberOfEpisodes: Boolean,
    val showBroadcastDate: Boolean,
    val showEpisodeNumber: Boolean,
    val showEpisodeTitle: Boolean,
    val showGroupedEpisodes: Boolean,
    val showSeason: Boolean,
    val showShortDescription: Boolean,
)

@Serializable
data class ProgramTag(
    val path: String,
    val name: String,
    val titlePath: String,
    val title: String,
    val parentTitle: String,
    val tagId: String,
    val description: String? = null,
    val nameSpaceName: String,
    val localTagId: String,
) {
    init {
        require(path.isNotEmpty()) { "ProgramTag: path should not be null" }
        require(name.isNotEmpty()) { "ProgramTag: name should not be null" }
        require(titlePath.isNotEmpty()) { "ProgramTag: titlePath should not be null" }
        require(title.isNotEmpty()) { "ProgramTag: title should not be null" }
        require(parentTitle.isNotEmpty()) { "ProgramTag: parentTitle should not be null" }
        require(tagId.isNotEmpty()) { "ProgramTag: tagId should not be null" }
        require(nameSpaceName.isNotEmpty()) { "ProgramTag: nameSpaceName should not be null" }
        require(localTagId.isNotEmpty()) { "ProgramTag: localTagId should not be null" }
    }
}

@Serializable
data class Chapter(
    val startTime: Int,
    val endTime: Int,
    val title: String,
    val id: String,
    val externalImageUrl: String? = null,
    val description: String? = null,
    val duration: String,
    val thumbnail: String,
    val startTimeInMillis: Double,
    val formattedStartTime: String,
) {
    init {
        require(title.isNotEmpty()) { "Chapter: title should not be null" }
        require(id.isNotEmpty()) { "Chapter: id should not be null" }
        require(duration.isNotEmpty()) { "Chapter: duration should not be null" }
        require(thumbnail.isNotEmpty()) { "Chapter: thumbnail should not be null" }
        require(formattedStartTime.isNotEmpty()) { "Chapter: formattedStartTime should not be null" }
    }
}

@Serializable
data class Tag(
    val path: String,
    val name: String,
    val titlePath: String,
    val title: String,
    val parentTitle: String,
    val tagId: String,
    val description: String? = null,
    val nameSpaceName: String,
    val localTagId: String,
) {
    init {
        require(path.isNotEmpty()) { "Tag: path should not be null" }
        require(name.isNotEmpty()) { "Tag: name should not be null" }
        require(titlePath.isNotEmpty()) { "Tag: titlePath should not be null" }
        require(title.isNotEmpty()) { "Tag: title should not be null" }
        require(parentTitle.isNotEmpty()) { "Tag: parentTitle should not be null" }
        require(tagId.isNotEmpty()) { "Tag: tagId should not be null" }
        require(nameSpaceName.isNotEmpty()) { "Tag: nameSpaceName should not be null" }
        require(localTagId.isNotEmpty()) { "Tag: localTagId should not be null" }
    }
}

@Serializable
data class Episode(
    val ageGroup: String? = null,
    val allowedRegion: String,
    val assetOffTime: String,
    val assetOnTime: String,
    val assetPath: String,
    val assetStatus: String,
    val brands: List<String>,
    val broadcastDate: Long,
    val categories: List<String>,
    val chapters: List<Chapter> = emptyList(),
    val description: String,
    val displayOptions: DisplayOptions,
    val duration: Int,
    val episodeNumber: Int,
    val externalPermalink: String,
    val formattedBroadcastDate: String,
    val formattedBroadcastFullDate: String,
    val formattedBroadcastShortDate: String,
    val name: String,
    val offTimeDate: Long? = null,
    val onTimeDate: Long? = null,
    val pageId: String,
    val pagePath: String,
    val path: String,
    val permalink: String,
    val productPlacement: Boolean? = null,
    val program: String,
    val programAlternativeImageUrl: String,
    val programBrands: List<String>,
    val programDescription: String,
    val programImageUrl: String,
    val programName: String,
    val programPath: String,
    val programTags: List<ProgramTag> = emptyList(),
    val programType: String,
    val programUrl: String,
    val programWhatsonId: String,
    val publicationId: String,
    val seasonName: String,
    val seasonNbOfEpisodes: Int,
    val seasonPath: String,
    val seasonTitle: String,
    val shortDescription: String,
    val subtitle: String,
    val tags: List<Tag>,
    val title: String,
    val transcodingStatus: String,
    val url: String,
    val videoId: String,
    val videoThumbnailUrl: String,
    val whatsonId: String,
    val instigator: String,
    val lastIndex: String,
    val id: String,
    val score: Double,
    val type: String,
)

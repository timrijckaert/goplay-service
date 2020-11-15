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
    val showBroadcastDate: Boolean,
    val showEpisodeNumber: Boolean,
    val showEpisodeTitle: Boolean,
    val showGroupedEpisodes: Boolean,
    val showSeason: Boolean,
    val showShortDescription: Boolean,
    val hideNumberOfEpisodes: Boolean? = null,
    val showMostRelevantEpisode: Boolean? = null,
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
    val allowedRegion: String,
    val assetOffTime: String,
    val assetOnTime: String,
    val assetStatus: String,
    val brands: List<String>,
    val broadcastDate: Long,
    val categories: List<String>,
    val displayOptions: DisplayOptions,
    val duration: Int,
    val episodeNumber: Int,
    val name: String,
    val pagePath: String,
    val path: String? = null,
    val program: String,
    val programDescription: String,
    val programImageUrl: String,
    val programName: String,
    val programPath: String,
    val programType: String,
    val programUrl: String,
    val publicationId: String,
    val seasonName: String,
    val seasonNbOfEpisodes: Int,
    val seasonPath: String,
    val seasonTitle: String,
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
    val programBrands: List<String> = emptyList(),
    val programTags: List<ProgramTag> = emptyList(),
    val tags: List<Tag> = emptyList(),
    val chapters: List<Chapter> = emptyList(),
    val pageId: String? = null,
    val shortDescription: String? = null,
    val description: String? = null,
    val formattedBroadcastShortDate: String? = null,
    val formattedBroadcastFullDate: String? = null,
    val formattedBroadcastDate: String? = null,
    val externalPermalink: String? = null,
    val programWhatsonId: String? = null,
    val permalink: String? = null,
    val assetPath: String? = null,
    val programAlternativeImageUrl: String? = null,
    val offTimeDate: Long? = null,
    val subtitle: String? = null,
    val ageGroup: String? = null,
    val onTimeDate: Long? = null,
    val productPlacement: Boolean? = null,
) {
    init {
        require(allowedRegion.isNotEmpty()) { "Episode: allowedRegion can not be empty" }
        require(assetOffTime.isNotEmpty()) { "Episode: assetOffTime can not be empty" }
        require(assetOnTime.isNotEmpty()) { "Episode: assetOnTime can not be empty" }
        require(assetStatus.isNotEmpty()) { "Episode: assetStatus can not be empty" }
        require(brands.isNotEmpty()) { "Episode: brands can not be empty" }
        require(categories.isNotEmpty()) { "Episode: categories can not be empty" }
        require(name.isNotEmpty()) { "Episode: name can not be empty" }
        require(pagePath.isNotEmpty()) { "Episode: pagePath can not be empty" }
        require(program.isNotEmpty()) { "Episode: program can not be empty" }
        require(programDescription.isNotEmpty()) { "Episode: programDescription can not be empty" }
        require(programImageUrl.isNotEmpty()) { "Episode: programImageUrl can not be empty" }
        require(programName.isNotEmpty()) { "Episode: programName can not be empty" }
        require(programPath.isNotEmpty()) { "Episode: programPath can not be empty" }
        require(programType.isNotEmpty()) { "Episode: programType can not be empty" }
        require(programUrl.isNotEmpty()) { "Episode: programUrl can not be empty" }
        require(publicationId.isNotEmpty()) { "Episode: publicationId can not be empty" }
        require(seasonName.isNotEmpty()) { "Episode: seasonName can not be empty" }
        require(seasonPath.isNotEmpty()) { "Episode: seasonPath can not be empty" }
        require(seasonTitle.isNotEmpty()) { "Episode: seasonTitle can not be empty" }
        require(title.isNotEmpty()) { "Episode: title can not be empty" }
        require(transcodingStatus.isNotEmpty()) { "Episode: transcodingStatus can not be empty" }
        require(url.isNotEmpty()) { "Episode: url can not be empty" }
        require(videoId.isNotEmpty()) { "Episode: videoId can not be empty" }
        require(videoThumbnailUrl.isNotEmpty()) { "Episode: videoThumbnailUrl can not be empty" }
        require(whatsonId.isNotEmpty()) { "Episode: whatsonId can not be empty" }
        require(instigator.isNotEmpty()) { "Episode: instigator can not be empty" }
        require(lastIndex.isNotEmpty()) { "Episode: lastIndex can not be empty" }
        require(id.isNotEmpty()) { "Episode: id can not be empty" }
        require(type.isNotEmpty()) { "Episode: type can not be empty" }
    }
}

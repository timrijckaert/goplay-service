package be.tapped.vrtnu.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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

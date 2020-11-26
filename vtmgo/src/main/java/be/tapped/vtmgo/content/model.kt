package be.tapped.vtmgo.content

import kotlinx.serialization.Serializable

@Serializable
data class Target(
    val type: String,
    val id: String,
    val name: String,
)

@Serializable
data class PagedTeaserContent(
    val title: String,
    val target: Target,
    val imageUrl: String,
    val geoBlocked: Boolean,
    val blockedFor: String? = null,
)

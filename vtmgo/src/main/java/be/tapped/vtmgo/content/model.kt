package be.tapped.vtmgo.content

import kotlinx.serialization.SerialName
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

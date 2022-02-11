package be.tapped.goplay.content

import kotlinx.serialization.Serializable

@Serializable
public data class Program(val id: String) {
    @Serializable
    public data class PageInfo(val nodeId: String)
}

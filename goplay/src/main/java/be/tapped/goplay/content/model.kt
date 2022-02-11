package be.tapped.goplay.content

import kotlinx.serialization.Serializable

@Serializable
public data class Program(val id: String) {
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

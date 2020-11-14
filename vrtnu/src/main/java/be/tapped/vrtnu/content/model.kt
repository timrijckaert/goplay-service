package be.tapped.vrtnu.content

data class Brand(val name: String)

data class AZProgram(
    val title: String,
    val type: String,
    val episodeCount: Int,
    val score: Double,
    val programUrl: String,
    val targetUrl: String,
    val programName: String,
    val thumbnail: String,
    val alternativeImage: String,
    val brands: List<Brand>,
    val description: String,
)

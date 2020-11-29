package be.tapped.vrtnu.epg

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Epg(
    @SerialName("O8")
    val een: List<EpgProgram>,
    @SerialName("1H")
    val canvas: List<EpgProgram>,
    @SerialName("O9")
    val ketnet: List<EpgProgram>,
)

@Serializable
data class EpgProgram(
    val start: String,
    val end: String,
    val startTime: String,
    val endTime: String,
    val title: String,
    @SerialName("vrt.whatson-id")
    val whatsonId: String? = null,
    val image: String? = null,
    val programWhatsonId: String? = null,
    val featured: Boolean? = null,
    val description: String? = null,
    val shortDescription: String? = null,
    val url: String? = null,
    val programPath: String? = null,
    val assetPath: String? = null,
    val subtitle: String? = null,
) {
    init {
        require(start.isNotEmpty()) { "start can not be blank." }
        require(end.isNotEmpty()) { "end can not be blank." }
        require(startTime.isNotEmpty()) { "startTime can not be blank." }
        require(endTime.isNotEmpty()) { "endTime can not be blank." }
        require(title.isNotEmpty()) { "title can not be blank." }
    }
}

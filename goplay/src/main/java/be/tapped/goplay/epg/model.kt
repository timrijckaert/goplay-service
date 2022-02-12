package be.tapped.goplay.epg

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class EpgProgram(
    @SerialName("program_title") val programTitle: String? = null,
    @SerialName("episode_title") val episodeTitle: String? = null,
    @SerialName("original_title") val originalTitle: String? = null,
    @SerialName("episode_nr") val episodeNr: String? = null,
    @SerialName("season") val season: String? = null,
    @SerialName("genre") val genre: String? = null,
    @SerialName("timestamp") val timestamp: Int,
    @SerialName("date_string") val date: String,
    @SerialName("time_string") val time: String,
    @SerialName("won_id") val wonId: String? = null,
    @SerialName("won_program_id") val wonProgramId: String? = null,
    @SerialName("program_concept") val programConcept: String? = null,
    @SerialName("content_episode") val contentEpisode: String? = null,
    @SerialName("duration") val duration: String? = null,
    @SerialName("program_node") val programName: ProgramNode? = null,
    @SerialName("video_node") val videoNode: VideoNode? = null,
)

@Serializable
public data class ProgramNode(val url: String)

@Serializable
public data class VideoNode(
    val description: String,
    val duration: Int,
    val image: String,
    @SerialName("latest_video") val latestVideo: Boolean,
    val created: Int,
    val title: String,
    val url: String,
)

public enum class GoPlayBrand {
    Play4,
    Play5,
    Play6,
    Play7;
}

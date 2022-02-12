package be.tapped.goplay.stream

import be.tapped.goplay.content.Program

public sealed interface ResolvedStream {
    public val videoUuid: Program.Detail.Playlist.Episode.VideoUuid

    public data class MpegDash(
        override val videoUuid: Program.Detail.Playlist.Episode.VideoUuid,
        val url: String,
        val auth: String
    ) : ResolvedStream {
        public val licenseUrl: String = "https://wv-keyos.licensekeyserver.com/"
    }

    public data class Hls(
        override val videoUuid: Program.Detail.Playlist.Episode.VideoUuid,
        val url: String
    ) : ResolvedStream
}

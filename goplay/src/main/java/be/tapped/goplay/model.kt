package be.tapped.goplay

import arrow.core.Nel
import be.tapped.goplay.content.Category
import be.tapped.goplay.content.Program
import be.tapped.goplay.epg.EpgProgram
import be.tapped.goplay.profile.TokenWrapper
import be.tapped.goplay.stream.ResolvedStream
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.JsonObject

public sealed interface Failure {
    public data class JsonParsingException(val throwable: Throwable) : Failure
    public data class HTMLJsonExtractionException(val throwable: Throwable) : Failure
    public data class Network(val throwable: Throwable) : Failure

    public sealed interface Authentication : Failure {
        public data class AWS(val statusCode: Int, val statusText: String?) : Authentication
        public object Login : Authentication
        public object Refresh : Authentication
        public object Profile : Authentication
    }

    public sealed interface Content : Failure {
        public object NoPrograms : Content
        public object NoCategories : Content
        public data class NoProgramsByCategory(val categoryId: Category.Id) : Content
    }

    public sealed interface Stream : Failure {
        public val videoUuid: Program.Detail.Playlist.Episode.VideoUuid

        public data class MpegDash(override val videoUuid: Program.Detail.Playlist.Episode.VideoUuid, val json: JsonObject, val throwable: Throwable) :
            Stream

        public data class DrmAuth(override val videoUuid: Program.Detail.Playlist.Episode.VideoUuid, val json: JsonObject, val throwable: Throwable) :
            Stream

        public data class Hls(override val videoUuid: Program.Detail.Playlist.Episode.VideoUuid, val json: JsonObject, val throwable: Throwable) : Stream
        public data class UnknownStream(override val videoUuid: Program.Detail.Playlist.Episode.VideoUuid, val json: JsonObject) : Stream
    }

    public sealed interface Epg : Failure {
        public data class NoEpgDataFor(val date: LocalDate) : Epg
    }
}

public data class Stream(val stream: ResolvedStream)

public data class Token(val token: TokenWrapper)
public data class Profile(val profile: be.tapped.goplay.profile.Profile) : Failure.Authentication

public data class Categories(val categories: Nel<Category>)

public data class Detail(val program: Program.Detail)
public data class AllPrograms(val programs: Nel<Program.Overview>)

public data class ProgramGuide(val epg: List<EpgProgram>)

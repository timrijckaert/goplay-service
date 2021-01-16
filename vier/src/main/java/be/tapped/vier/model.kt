package be.tapped.vier

import arrow.core.NonEmptyList
import be.tapped.vier.content.M3U8Stream
import be.tapped.vier.content.Program
import be.tapped.vier.content.SearchHit
import be.tapped.vier.content.VideoUuid
import be.tapped.vier.epg.EpgProgram
import be.tapped.vier.profile.TokenWrapper
import okhttp3.Request
import java.util.*

public sealed class ApiResponse {
    public sealed class Success : ApiResponse() {
        public sealed class Authentication : Success() {
            public data class Token(val token: TokenWrapper) : Authentication()
            public data class Profile(val profile: be.tapped.vier.profile.Profile) : Authentication()
        }

        public sealed class Content : Success() {
            public data class SingleEpisode(val episode: Program.Playlist.Episode) : Content()
            public data class SingleProgram(val program: Program) : Content()
            public data class Programs(val programs: List<Program>) : Content()
            public data class SearchResults(val hits: List<SearchHit>) : Content()
        }

        public data class Stream(val m3U8Stream: M3U8Stream) : Success()

        public data class ProgramGuide(val epg: List<EpgProgram>) : Success()
    }

    public sealed class Failure : ApiResponse() {
        public data class NetworkFailure(val responseCode: Int, val request: Request) : Failure()
        public data class JsonParsingException(val throwable: Throwable) : Failure()

        public sealed class HTML : Failure() {
            public object EmptyHTML : HTML()
            public data class MissingAttributeValue(public val attribute: String) : HTML()
            public data class NoSelection(public val cssQuery: String) : HTML()
            public data class NoChildAtPosition(public val position: Int, public val amountOfChildren: Int) : HTML()
            public data class Parsing(public val failures: NonEmptyList<HTML>) : HTML()
        }

        public sealed class Authentication : Failure() {
            public data class AWS(val statusCode: Int, val statusText: String?) : Authentication()
            public object Login : Authentication()
            public object Refresh : Authentication()
            public object Profile : Authentication()
        }

        public sealed class Content : Failure() {
            public object NoEpisodeFound : Content()
        }

        public sealed class Stream : Failure() {
            public data class NoStreamFound(val videoUuid: VideoUuid) : Stream()
        }

        public sealed class Epg : Failure() {
            public data class NoEpgDataFor(val calendar: Calendar) : Epg()
        }
    }
}

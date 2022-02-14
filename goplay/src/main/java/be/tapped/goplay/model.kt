package be.tapped.goplay

import be.tapped.goplay.content.Program
import be.tapped.goplay.epg.EpgProgram
import be.tapped.goplay.profile.TokenWrapper
import kotlinx.datetime.LocalDate

public sealed interface ApiResponse {
    public sealed interface Success : ApiResponse {
        public sealed interface Authentication : Success {
            public data class Token(val token: TokenWrapper) : Authentication
            public data class Profile(val profile: be.tapped.goplay.profile.Profile) : Authentication
        }

        public sealed interface Content : Success {
            public data class Programs(val programs: List<Program>) : Content
        }

        public data class ProgramGuide(val epg: List<EpgProgram>) : Success
    }

    public sealed interface Failure : ApiResponse {
        public data class JsonParsingException(val throwable: Throwable) : Failure
        public data class Network(val throwable: Throwable) : Failure

        public sealed interface Authentication : Failure {
            public data class AWS(val statusCode: Int, val statusText: String?) : Authentication
            public object Login : Authentication
            public object Refresh : Authentication
            public object Profile : Authentication
        }

        public sealed interface Content : Failure {
            public object ProgramNoLongerAvailable : Content
            public object NoEpisodeFound : Content
        }

        public sealed interface Epg : Failure {
            public data class NoEpgDataFor(val date: LocalDate) : Epg
        }
    }
}

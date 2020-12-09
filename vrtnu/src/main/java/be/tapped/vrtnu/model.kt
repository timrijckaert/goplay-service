package be.tapped.vrtnu

import arrow.core.NonEmptyList
import be.tapped.vrtnu.content.Category
import be.tapped.vrtnu.content.Episode
import be.tapped.vrtnu.content.Program
import be.tapped.vrtnu.content.StreamInformation
import be.tapped.vrtnu.epg.Epg
import be.tapped.vrtnu.profile.*
import okhttp3.Request

public sealed class ApiResponse {
    public sealed class Success : ApiResponse() {
        public sealed class Content : Success() {
            public data class Programs(val programs: List<Program>) : Content()
            public data class SingleProgram(val program: Program?) : Content()
            public data class Categories(val categories: List<Category>) : Content()
            public data class Episodes(val episodes: List<Episode>) : Content()
            public data class StreamInfo(val info: StreamInformation) : Content()
        }

        public data class ProgramGuide(val epg: Epg) : Success()

        public sealed class Authentication : Success() {
            public data class Token(val tokenWrapper: TokenWrapper) : Authentication()
            public data class PlayerToken(val vrtPlayerToken: VRTPlayerToken) : Authentication()
            public data class VRTToken(val xVRTToken: XVRTToken) : Authentication()
            public data class Favorites(val favorites: FavoriteWrapper) : Authentication()
        }
    }

    public sealed class Failure : ApiResponse() {
        public data class NetworkFailure(val responseCode: Int, val request: Request) : Failure()
        public data class JsonParsingException(val throwable: Throwable) : Failure()
        public object EmptyJson : Failure()

        public sealed class Authentication : Failure() {
            public data class FailedToLogin(val loginResponseFailure: LoginFailure) : Authentication()
            public data class MissingCookieValues(val cookieValues: NonEmptyList<String>) : Authentication()
        }
    }
}

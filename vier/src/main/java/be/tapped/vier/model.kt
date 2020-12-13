package be.tapped.vier

import arrow.core.NonEmptyList
import be.tapped.vier.content.M3U8Stream
import be.tapped.vier.content.Program
import be.tapped.vier.content.SearchHit
import be.tapped.vier.content.VideoUuid
import be.tapped.vier.profile.AccessToken
import be.tapped.vier.profile.IdToken
import be.tapped.vier.profile.RefreshToken
import okhttp3.Request

public sealed class ApiResponse {
    public sealed class Success : ApiResponse() {
        public sealed class Authentication : Success() {
            public data class Token(
                val accessToken: AccessToken,
                val expiresIn: Int,
                val tokenType: String,
                val refreshToken: RefreshToken,
                val idToken: IdToken,
            ) : Authentication()

            public data class Profile(
                val username: String,
                val sub: String? = null,
                val birthDate: String? = null,
                val gender: String? = null,
                val postalCode: String? = null,
                val selligentId: String? = null,
                val name: String? = null,
                val familyName: String? = null,
                val email: String? = null,
            ) : Authentication()
        }

        public sealed class Content : Success() {
            public data class Programs(val programs: List<Program>) : Content()
            public data class SearchResults(val hits: List<SearchHit>) : Content()
        }

        public data class Stream(val m3U8Stream: M3U8Stream) : Success()
    }

    public sealed class Failure : ApiResponse() {
        public data class NetworkFailure(val responseCode: Int, val request: Request) : Failure()
        public data class JsonParsingException(val throwable: Throwable) : Failure()

        public sealed class HTML : Failure() {
            public object EmptyHTML : HTML()
            public data class MissingAttributeValue(public val element: String, public val attribute: String) : HTML()
            public data class NoSelection(public val element: String, public val cssQuery: String) : HTML()
            public data class NoChildAtPosition(public val element: String, public val position: Int, public val amountOfChildren: Int) : HTML()
            public data class Parsing(public val failures: NonEmptyList<HTML>) : HTML()
        }

        public sealed class Authentication : Failure() {
            public data class AWS(val statusCode: Int, val statusText: String?) : Authentication()
            public object Login : Authentication()
            public object Refresh : Authentication()
            public object Profile : Authentication()
        }

        public sealed class Stream : Failure() {
            public data class NoStreamFound(val videoUuid: VideoUuid) : Stream()
        }
    }
}

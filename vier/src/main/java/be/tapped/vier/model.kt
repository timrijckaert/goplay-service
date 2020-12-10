package be.tapped.vier

import be.tapped.vier.content.Program
import okhttp3.Request

public sealed class ApiResponse {
    public sealed class Success : ApiResponse() {
        public sealed class Authentication : Success() {
            public data class Token(
                val accessToken: String,
                val expiresIn: Int,
                val tokenType: String,
                val refreshToken: String,
                val idToken: String,
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
        }
    }

    public sealed class Failure : ApiResponse() {
        public data class NetworkFailure(val responseCode: Int, val request: Request) : Failure()
        public data class JsonParsingException(val throwable: Throwable) : Failure()
        public object EmptyHTML : Failure()

        public sealed class Authentication : Failure() {
            public data class AWS(val statusCode: Int, val statusText: String?) : Authentication()
            public object Login : Authentication()
            public object Refresh : Authentication()
            public object Profile : Authentication()
        }
    }
}

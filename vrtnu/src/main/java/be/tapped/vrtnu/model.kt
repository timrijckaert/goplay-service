package be.tapped.vrtnu

import arrow.core.NonEmptyList
import be.tapped.vrtnu.content.Category
import be.tapped.vrtnu.content.Episode
import be.tapped.vrtnu.content.Program
import be.tapped.vrtnu.content.StreamInformation
import be.tapped.vrtnu.epg.Epg
import be.tapped.vrtnu.profile.FavoriteWrapper
import be.tapped.vrtnu.profile.LoginFailure
import be.tapped.vrtnu.profile.TokenWrapper
import be.tapped.vrtnu.profile.VRTPlayerToken
import be.tapped.vrtnu.profile.XVRTToken
import okhttp3.Request

sealed class ApiResponse {
    sealed class Success : ApiResponse() {
        sealed class Content : Success() {
            data class Programs(val programs: List<Program>) : Content()
            data class SingleProgram(val program: Program) : Content()
            data class Categories(val categories: List<Category>) : Content()
            data class Episodes(val episodes: List<Episode>) : Content()
            data class StreamInfo(val info: StreamInformation) : Content()
        }

        data class ProgramGuide(val epg: Epg) : Success()

        sealed class Authentication : Success() {
            data class Token(val tokenWrapper: TokenWrapper) : Authentication()
            data class PlayerToken(val vrtPlayerToken: VRTPlayerToken) : Authentication()
            data class VRTToken(val xVRTToken: XVRTToken) : Authentication()
            data class Favorites(val favorites: FavoriteWrapper) : Authentication()
        }
    }

    sealed class Failure : ApiResponse() {
        data class NetworkFailure(val responseCode: Int, val request: Request) : Failure()
        data class JsonParsingException(val throwable: Throwable) : Failure()
        object EmptyJson : Failure()

        sealed class Authentication : Failure() {
            data class FailedToLogin(val loginResponseFailure: LoginFailure) : Authentication()
            data class MissingCookieValues(val cookieValues: NonEmptyList<String>) : Authentication()
        }
    }
}

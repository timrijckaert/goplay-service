package be.tapped.vtmgo

import arrow.core.NonEmptyList
import be.tapped.vtmgo.content.Category
import be.tapped.vtmgo.content.LiveChannel
import be.tapped.vtmgo.content.PagedTeaserContent
import be.tapped.vtmgo.profile.JWT
import okhttp3.Request

sealed class ApiResponse {
    sealed class Success : ApiResponse() {
        sealed class Content : Success() {
            data class Catalog(val catalog: List<PagedTeaserContent>) : Content()
            data class Categories(val categories: List<Category>) : Content()
            data class LiveChannels(val channels: List<LiveChannel>) : Content()
        }

        sealed class Authentication : ApiResponse() {
            data class Token(val jwt: JWT) : Authentication()
        }
    }

    sealed class Failure : ApiResponse() {
        data class NetworkFailure(val responseCode: Int, val request: Request) : Failure()
        data class JsonParsingException(val throwable: Throwable) : Failure()
        object EmptyJson : Failure()

        sealed class Authentication : Failure() {
            data class MissingCookieValues(val cookieValues: NonEmptyList<String>) : Authentication()
            object NoAuthorizeResponse : Authentication()
            object NoCodeFound : Authentication()
            object NoStateFound : Authentication()
            object JWTTokenNotValid : Authentication()
        }
    }
}

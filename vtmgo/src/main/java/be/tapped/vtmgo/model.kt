package be.tapped.vtmgo

import arrow.core.NonEmptyList
import be.tapped.vtmgo.content.*
import be.tapped.vtmgo.epg.Epg
import be.tapped.vtmgo.profile.JWT
import okhttp3.Request

sealed class ApiResponse {
    sealed class Success : ApiResponse() {
        sealed class Content : Success() {
            data class Catalog(val catalog: List<PagedTeaserContent>) : Content()
            data class Categories(val categories: List<Category>) : Content()
            data class LiveChannels(val channels: List<LiveChannel>) : Content()
            data class StoreFrontRows(val rows: List<StoreFront>) : Content()
            data class Programs(val program: Program) : Content()
            data class Favorites(val favorites: StoreFront.MyListStoreFront) : Content()
            data class Search(val search: List<SearchResultResponse>) : Content()
        }

        data class ProgramGuide(val epg: Epg) : Success()

        sealed class Authentication : ApiResponse() {
            data class Token(val jwt: JWT) : Authentication()
        }

        sealed class Stream : Success() {
            data class Live(val anvatoStreamWrapper: AnvatoStreamWrapper) : Stream()
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

        sealed class Stream : Failure() {
            object NoAnvatoStreamFound : Stream()
            object NoJSONFoundInAnvatoJavascriptFunction : Stream()
            object NoPublishedEmbedUrlFound : Stream()
            object NoMPDManifestUrlFound : Stream()
        }
    }
}

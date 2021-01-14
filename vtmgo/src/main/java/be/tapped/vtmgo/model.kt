package be.tapped.vtmgo

import arrow.core.NonEmptyList
import be.tapped.vtmgo.content.AnvatoStream
import be.tapped.vtmgo.content.Category
import be.tapped.vtmgo.content.HlsCertificate
import be.tapped.vtmgo.content.HlsUrl
import be.tapped.vtmgo.content.LicenseUrl
import be.tapped.vtmgo.content.LiveChannel
import be.tapped.vtmgo.content.MPDUrl
import be.tapped.vtmgo.content.PagedTeaserContent
import be.tapped.vtmgo.content.Program
import be.tapped.vtmgo.content.SearchResultResponse
import be.tapped.vtmgo.content.StoreFront
import be.tapped.vtmgo.content.Subtitle
import be.tapped.vtmgo.epg.Epg
import be.tapped.vtmgo.profile.Profile
import be.tapped.vtmgo.profile.TokenWrapper
import okhttp3.Request

public sealed class ApiResponse {
    public sealed class Success : ApiResponse() {
        public sealed class Content : Success() {
            public data class Catalog(val catalog: List<PagedTeaserContent>) : Content()
            public data class Categories(val categories: List<Category>) : Content()
            public data class LiveChannels(val channels: List<LiveChannel>) : Content()
            public data class StoreFrontRows(val rows: List<StoreFront>) : Content()
            public data class Programs(val program: Program) : Content()
            public data class Favorites(val favorites: StoreFront.MyListStoreFront?) : Content()
            public data class Search(val search: List<SearchResultResponse>) : Content()
        }

        public data class ProgramGuide(val epg: Epg) : Success()

        public sealed class Authentication : ApiResponse() {
            public data class Token(val token: TokenWrapper) : Authentication()
            public data class Profiles(val profiles: List<Profile>) : Authentication()
        }

        public sealed class Stream : ApiResponse() {
            public data class Anvato(val stream: AnvatoStream) : Stream()
            public data class Dash(val url: MPDUrl, val licenseUrl: LicenseUrl, val subtitle: List<Subtitle>) : Stream()
            public data class Hls(val url: HlsUrl, val licenseUrl: LicenseUrl, val hlsCertificate: HlsCertificate, val subtitle: List<Subtitle>) :
                Stream()
        }
    }

    public sealed class Failure : ApiResponse() {
        public data class NetworkFailure(val responseCode: Int, val request: Request) : Failure()
        public data class JsonParsingException(val throwable: Throwable) : Failure()
        public object EmptyJson : Failure()

        public sealed class Authentication : Failure() {
            public data class MissingCookieValues(val cookieValues: NonEmptyList<String>) : Authentication()
            public object NoAuthorizeResponse : Authentication()
            public object NoCodeFound : Authentication()
            public object NoStateFound : Authentication()
        }

        public sealed class Stream : Failure() {
            public data class NoStreamFoundForType(val streamType: String) : Stream()
            public object NoJSONFoundInAnvatoJavascriptFunction : Stream()
            public object NoDashStreamFound : Stream()
            public object NoHlsStreamFound : Stream()
            public object NoAnvatoStreamFound : Stream()
            public object NoPublishedEmbedUrlFound : Stream()
            public object NoMPDManifestUrlFound : Stream()
        }
    }
}

package be.tapped.vtmgo

import be.tapped.vtmgo.content.Category
import be.tapped.vtmgo.content.PagedTeaserContent
import okhttp3.Request

sealed class ApiResponse {
    sealed class Success : ApiResponse() {
        data class Programs(val programResponse: List<PagedTeaserContent>) : Success()
        data class Categories(val categories: List<Category>) : Success()
    }

    sealed class Failure : ApiResponse() {
        data class NetworkFailure(val responseCode: Int, val request: Request) : Failure()
        data class JsonParsingException(val throwable: Throwable) : Failure()
        object EmptyJson : Failure()
    }
}

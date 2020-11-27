package be.tapped.vrtnu

import be.tapped.vrtnu.content.Category
import be.tapped.vrtnu.content.Episode
import be.tapped.vrtnu.content.Program
import be.tapped.vrtnu.content.StreamInformation
import okhttp3.Request

sealed class ApiResponse {
    sealed class Success : ApiResponse() {
        data class Programs(val programs: List<Program>) : Success()
        data class SingleProgram(val program: Program) : Success()
        data class Categories(val categories: List<Category>) : Success()
        data class Episodes(val episodes: List<Episode>) : Success()
        data class StreamInfo(val info: StreamInformation) : Success()
    }

    sealed class Failure : ApiResponse() {
        data class NetworkFailure(val responseCode: Int, val request: Request) : Failure()
        data class JsonParsingException(val throwable: Throwable) : Failure()
        object EmptyJson : Failure()
    }
}

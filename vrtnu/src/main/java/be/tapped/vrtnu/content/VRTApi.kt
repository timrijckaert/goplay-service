package be.tapped.vrtnu.content

import be.tapped.vrtnu.common.defaultOkHttpClient
import okhttp3.OkHttpClient
import okhttp3.Request

sealed class ApiResponse {
    sealed class Success : ApiResponse() {
        data class Programs(val programs: List<Program>) : Success()
        data class SingleProgram(val program: Program) : Success()
        data class Categories(val categories: List<Category>) : Success()
        data class Episodes(val episodes: List<Episode>) : Success()
        data class StreamInfo(val info : StreamInformation) : Success()
    }

    sealed class Failure : ApiResponse() {
        data class NetworkFailure(val responseCode: Int, val request: Request) : Failure()
        data class JsonParsingException(val throwable: Throwable) : Failure()
        object EmptyJson : Failure()
    }
}

class VRTApi(
    client: OkHttpClient = defaultOkHttpClient,
    programRepo: ProgramRepo = HttpProgramRepo(client, JsonProgramParser()),
    categoryRepo: CategoryRepo = HttpCategoryRepo(client, JsonCategoryParser()),
    episodeRepo: EpisodeRepo = HttpEpisodeRepo(client, JsonEpisodeParser()),
    streamRepo: StreamRepo = HttpStreamRepo(client, JsonStreamInformationParser()),
    screenshotRepo: ScreenshotRepo = DefaultScreenshotRepo,
) : ProgramRepo by programRepo,
    CategoryRepo by categoryRepo,
    EpisodeRepo by episodeRepo,
    StreamRepo by streamRepo,
    ScreenshotRepo by screenshotRepo

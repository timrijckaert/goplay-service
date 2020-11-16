package be.tapped.vrtnu.content

import be.tapped.vrtnu.common.defaultOkHttpClient
import okhttp3.OkHttpClient

sealed class ApiResponse {
    sealed class Success : ApiResponse() {
        data class AlphabeticPrograms(val programs: List<Program>) : Success()
        data class Categories(val categories: List<Category>) : Success()
        data class Episodes(val episodes: List<Episode>) : Success()
    }

    sealed class Failure : ApiResponse() {
        data class JsonParsingException(val throwable: Throwable) : Failure()
        object EmptyJson : Failure()
    }
}

class VRTApi(
    client: OkHttpClient = defaultOkHttpClient,
    azRepo: AZRepo = HttpAZRepo(client, JsonAZProgramParser()),
    categoryRepo: CategoryRepo = HttpCategoryRepo(client, JsonCategoryParser()),
    elasticSearchRepo: ElasticSearchRepo = HttpElasticSearchRepo(client, JsonEpisodeParser()),
    screenshotRepo: ScreenshotRepo = DefaultScreenshotRepo,
) : AZRepo by azRepo,
    CategoryRepo by categoryRepo,
    ElasticSearchRepo by elasticSearchRepo,
    ScreenshotRepo by screenshotRepo

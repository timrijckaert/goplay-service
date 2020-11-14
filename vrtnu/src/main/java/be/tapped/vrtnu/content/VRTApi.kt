package be.tapped.vrtnu.content

import be.tapped.vrtnu.common.defaultOkHttpClient
import okhttp3.OkHttpClient

sealed class ApiResponse {
    sealed class Success : ApiResponse() {
        data class AlphabeticPrograms(val programs: List<AZProgram>) : Success()
        data class Categories(val categories: List<Category>) : Success()
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
) : AZRepo by azRepo,
    CategoryRepo by categoryRepo

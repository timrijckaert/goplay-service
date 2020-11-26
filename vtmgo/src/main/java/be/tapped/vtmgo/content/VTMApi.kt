package be.tapped.vtmgo.content

import be.tapped.vtmgo.common.AuthorizationHeaderBuilder
import be.tapped.vtmgo.common.HeaderBuilder
import be.tapped.vtmgo.common.defaultOkHttpClient
import okhttp3.OkHttpClient

sealed class ApiResponse {
    sealed class Success : ApiResponse() {
        data class Programs(val programResponse: List<PagedTeaserContent>) : Success()
    }

    sealed class Failure : ApiResponse() {
        data class JsonParsingException(val throwable: Throwable) : Failure()
        object EmptyJson : Failure()
    }
}

class VTMApi(
    client: OkHttpClient = defaultOkHttpClient,
    headerBuilder: HeaderBuilder = AuthorizationHeaderBuilder(),
    programRepo: ProgramRepo = HttpProgramRepo(client, headerBuilder, JsonPagedTeaserContentParser()),
) : ProgramRepo by programRepo

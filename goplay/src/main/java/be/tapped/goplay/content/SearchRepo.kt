package be.tapped.goplay.content

import arrow.core.Either
import arrow.core.computations.either
import be.tapped.goplay.ApiResponse
import be.tapped.goplay.common.ktorClient
import io.ktor.client.HttpClient

public sealed interface SearchRepo {
    public suspend fun search(query: String): Either<ApiResponse.Failure, ApiResponse.Success.Content.SearchResults>
}

internal class HttpSearchRepo(private val client: HttpClient = ktorClient) : SearchRepo {

    // curl -X POST \
    // -H  -d '{ "query": <query>,"sites":["vier", "vijf", "zes"],"page":0,"mode":"byDate"}' "https://api.viervijfzes.be/search
    override suspend fun search(query: String): Either<ApiResponse.Failure, ApiResponse.Success.Content.SearchResults> =
        either {
            TODO("The previous implementation was broken and needs to be re-implemented")
        }
}

package be.tapped.goplay.content

import arrow.core.Either
import be.tapped.goplay.ApiResponse

public fun interface SearchRepo {
    public suspend fun search(query: String): Either<ApiResponse.Failure, ApiResponse.Success.Content.SearchResults>
}

// curl -X POST \
// -H  -d '{ "query": <query>,"sites":["vier", "vijf", "zes"],"page":0,"mode":"byDate"}' "https://api.viervijfzes.be/search
internal fun httpSearchRepo(): SearchRepo =
    SearchRepo {
        TODO("The previous implementation was broken and needs to be re-implemented")
    }

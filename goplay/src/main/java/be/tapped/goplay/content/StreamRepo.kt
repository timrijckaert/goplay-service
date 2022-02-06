package be.tapped.goplay.content

import arrow.core.Either
import be.tapped.goplay.ApiResponse
import be.tapped.goplay.profile.IdToken

public fun interface StreamRepo {
    public suspend fun streamByVideoUuid(idToken: IdToken, videoUuid: VideoUuid): Either<ApiResponse.Failure, ApiResponse.Success.Stream>
}

// curl -X GET \
// -H "Authorization: <IdToken>" \
// -H "https://api.viervijfzes.be/content/<VideoUuid>"
internal fun httpStreamRepo(): StreamRepo =
    StreamRepo { idToken, videoUuid ->
        TODO()
    }

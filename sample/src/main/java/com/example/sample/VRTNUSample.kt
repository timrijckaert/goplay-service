package com.example.sample

import arrow.core.Either
import arrow.core.Tuple5
import be.tapped.vrtnu.authentication.ProfileRepo
import be.tapped.vrtnu.authentication.ProfileResponse
import be.tapped.vrtnu.authentication.RefreshToken
import be.tapped.vrtnu.content.ElasticSearchQueryBuilder
import be.tapped.vrtnu.content.VRTApi
import kotlinx.coroutines.flow.toList
import okhttp3.OkHttpClient
import java.util.logging.Level
import java.util.logging.Logger

suspend fun main(args: Array<String>) {
    Logger.getLogger(OkHttpClient::class.java.name).level = Level.FINE

    val userName = args[0]
    val password = args[1]
    val authenticationProvider = ProfileRepo()

    // Authentication
    val authenticationTokens = authentication(authenticationProvider, userName, password)

    // API
    apiSamples(authenticationTokens)
}

private suspend fun authentication(
    profileRepo: ProfileRepo,
    userName: String,
    password: String,
): Tuple5<Either<ProfileResponse.Failure, ProfileResponse.Success.Token>, RefreshToken, Either<ProfileResponse.Failure, ProfileResponse.Success.Token>, Either<ProfileResponse.Failure, ProfileResponse.Success.VRTToken>, Either<ProfileResponse.Failure, ProfileResponse.Success.PlayerToken>> {
    val tokenWrapperResult = profileRepo.fetchTokenWrapper(userName, password)
    val refreshToken = tokenWrapperResult.orNull()!!.tokenWrapper.refreshToken
    val newTokenWrapperResult = profileRepo.refreshTokenWrapper(refreshToken)
    val xVRTToken = profileRepo.fetchXVRTToken(userName, password)
    val vrtPlayerToken = profileRepo.fetchVRTPlayerToken(xVRTToken.orNull()!!.xVRTToken)
    return Tuple5(tokenWrapperResult, refreshToken, newTokenWrapperResult, xVRTToken, vrtPlayerToken)
}

private suspend fun apiSamples(authenticationTokens: Tuple5<Either<ProfileResponse.Failure, ProfileResponse.Success.Token>, RefreshToken, Either<ProfileResponse.Failure, ProfileResponse.Success.Token>, Either<ProfileResponse.Failure, ProfileResponse.Success.VRTToken>, Either<ProfileResponse.Failure, ProfileResponse.Success.PlayerToken>>) {
    val vrtApi = VRTApi()
    // A-Z
    val azPrograms = vrtApi.fetchAZPrograms()
    println(azPrograms)

    // Categories
    val categories = vrtApi.fetchCategories()
    println(categories)

    // Search
    val allSearchResults = vrtApi.episodes(ElasticSearchQueryBuilder.SearchQuery(category = "cultuur")).toList()
    println(allSearchResults)

    // Single Program
    val programName = "terzake"
    val episodesForProgram = vrtApi.fetchProgramByName(programName).map { vrtApi.episodesForProgram(it.program).toList() }
    println(episodesForProgram)

    // Fetch Streams
    val vrtPlayerToken = authenticationTokens.e.orNull()!!.vrtPlayerToken
    val latestAiredEpisode = episodesForProgram.orNull()!!.first().orNull()!!.episodes.first()
    val latestAiredEpisodeStreamInfo = vrtApi.getVODStream(vrtPlayerToken, latestAiredEpisode.videoId, latestAiredEpisode.publicationId)
    println(latestAiredEpisodeStreamInfo)
}

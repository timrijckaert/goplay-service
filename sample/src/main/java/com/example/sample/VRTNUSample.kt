package com.example.sample

import arrow.core.Either
import arrow.core.Tuple5
import be.tapped.vrtnu.authentication.AuthenticationProvider
import be.tapped.vrtnu.authentication.RefreshToken
import be.tapped.vrtnu.authentication.TokenRepo
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
    val authenticationProvider = AuthenticationProvider()

    // Authentication
    val authenticationTokens = authentication(authenticationProvider, userName, password)

    // API
    apiSamples(authenticationTokens)
}

private suspend fun authentication(
    authenticationProvider: AuthenticationProvider,
    userName: String,
    password: String,
): Tuple5<Either<TokenRepo.TokenResponse.Failure, TokenRepo.TokenResponse.Success.Token>, RefreshToken, Either<TokenRepo.TokenResponse.Failure, TokenRepo.TokenResponse.Success.Token>, Either<TokenRepo.TokenResponse.Failure, TokenRepo.TokenResponse.Success.VRTToken>, Either<TokenRepo.TokenResponse.Failure, TokenRepo.TokenResponse.Success.PlayerToken>> {
    val tokenWrapperResult = authenticationProvider.fetchTokenWrapper(userName, password)
    val refreshToken = tokenWrapperResult.orNull()!!.tokenWrapper.refreshToken
    val newTokenWrapperResult = authenticationProvider.refreshTokenWrapper(refreshToken)
    val xVRTToken = authenticationProvider.fetchXVRTToken(userName, password)
    val vrtPlayerToken = authenticationProvider.fetchVRTPlayerToken(xVRTToken.orNull()!!.xVRTToken)
    return Tuple5(tokenWrapperResult, refreshToken, newTokenWrapperResult, xVRTToken, vrtPlayerToken)
}

private suspend fun apiSamples(authenticationTokens: Tuple5<Either<TokenRepo.TokenResponse.Failure, TokenRepo.TokenResponse.Success.Token>, RefreshToken, Either<TokenRepo.TokenResponse.Failure, TokenRepo.TokenResponse.Success.Token>, Either<TokenRepo.TokenResponse.Failure, TokenRepo.TokenResponse.Success.VRTToken>, Either<TokenRepo.TokenResponse.Failure, TokenRepo.TokenResponse.Success.PlayerToken>>) {
    val vrtApi = VRTApi()
    // A-Z
    val azPrograms = vrtApi.fetchAZPrograms()
    println(azPrograms)

    // Categories
    val categories = vrtApi.fetchCategories()
    println(categories)

    // Search
    val allSearchResults = vrtApi.episodes(ElasticSearchQueryBuilder.SearchQuery(category = "cultuur")).toList()
    // println(allSearchResults)

    // Single Program
    // val programName = "Geubels en de Hollanders"
    // val geubelsEnDeHollanders = vrtApi.fetchProgramByName(programName).map { vrtApi.episodesForProgram(it.program).toList() }
    // println(geubelsEnDeHollanders)

    // Fetch Streams
    // val vrtPlayerToken = authenticationTokens.e.orNull()!!.vrtPlayerToken
    // val firstGeubelsEnDeHollandersEpisode = geubelsEnDeHollanders.orNull()!!.first().orNull()!!.episodes.first()
    // val firstGeubelsEnDeHollandersEpisodeStreamInfo = vrtApi.getVODStream(vrtPlayerToken, firstGeubelsEnDeHollandersEpisode.videoId, firstGeubelsEnDeHollandersEpisode.publicationId)
    // println(firstGeubelsEnDeHollandersEpisodeStreamInfo)
}

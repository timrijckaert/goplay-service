package com.example.sample

import arrow.core.Tuple6
import be.tapped.vrtnu.content.ElasticSearchQueryBuilder
import be.tapped.vrtnu.content.LiveStreams
import be.tapped.vrtnu.content.VRTApi
import be.tapped.vrtnu.epg.HttpEpgRepo
import be.tapped.vrtnu.profile.FavoriteWrapper
import be.tapped.vrtnu.profile.ProfileRepo
import be.tapped.vrtnu.profile.RefreshToken
import be.tapped.vrtnu.profile.TokenWrapper
import be.tapped.vrtnu.profile.VRTPlayerToken
import be.tapped.vrtnu.profile.XVRTToken
import kotlinx.coroutines.flow.toList
import okhttp3.OkHttpClient
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

public suspend fun main(args: Array<String>) {
    Logger.getLogger(OkHttpClient::class.java.name).level = Level.FINE

    val userName = args[0]
    val password = args[1]
    val authenticationProvider = ProfileRepo()

    // Authentication
    val authenticationTokens = authentication(authenticationProvider, userName, password)

    // API
    apiSamples(authenticationTokens)

    // EPG
    epg()
}

private suspend fun authentication(
    profileRepo: ProfileRepo,
    userName: String,
    password: String,
): Tuple6<TokenWrapper, RefreshToken, TokenWrapper, XVRTToken, VRTPlayerToken, FavoriteWrapper> {
    // Token wrapper
    val tokenWrapperResult = profileRepo.fetchTokenWrapper(userName, password)

    // Refresh token with refresh token
    val refreshToken = tokenWrapperResult.orNull()!!.tokenWrapper.refreshToken
    val newTokenWrapperResult = profileRepo.refreshTokenWrapper(refreshToken)

    // X-VRT-Token
    val xVRTToken = profileRepo.fetchXVRTToken(userName, password)

    // VRT Player Token
    val vrtPlayerToken = profileRepo.fetchVRTPlayerToken(xVRTToken.orNull()!!.xVRTToken)

    // Favorites
    val forceUnwrappedXVRTToken = xVRTToken.orNull()!!.xVRTToken
    val userFavorites = profileRepo.favorites(forceUnwrappedXVRTToken)

    return Tuple6(
        tokenWrapperResult.orNull()!!.tokenWrapper,
        refreshToken,
        newTokenWrapperResult.orNull()!!.tokenWrapper,
        xVRTToken.orNull()!!.xVRTToken,
        vrtPlayerToken.orNull()!!.vrtPlayerToken,
        userFavorites.orNull()!!.favorites
    )
}

private suspend fun apiSamples(tokenTuple: Tuple6<TokenWrapper, RefreshToken, TokenWrapper, XVRTToken, VRTPlayerToken, FavoriteWrapper>) {
    val (tokenWrapper, refreshToken, refreshedTokenWrapper, xVRTToken, vrtPlayerToken, favorites) = tokenTuple

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
    val programWithEpisodes = vrtApi.fetchProgramByName(programName).map { it.program to vrtApi.episodesForProgram(it.program!!).toList() }
    val forceUnwrappedProgramWithEpisodes = programWithEpisodes.orNull()!!
    val program = forceUnwrappedProgramWithEpisodes.first
    val episodes = forceUnwrappedProgramWithEpisodes.second.flatMap { it.orNull()!!.episodes }
    println(program)
    println(episodes)

    // Fetch Video on Demand Streams
    val latestAiredEpisode = episodes.first()
    println(latestAiredEpisode)
    val latestAiredEpisodeStreamInfo = vrtApi.getStream(vrtPlayerToken, latestAiredEpisode.videoId, latestAiredEpisode.publicationId)
    println(latestAiredEpisodeStreamInfo)

    // Fetch Live Stream Video
    val vrtNWSLiveStreamInfo = vrtApi.getStream(vrtPlayerToken, LiveStreams.een.videoId)
    println(vrtNWSLiveStreamInfo)
}

private suspend fun epg() {
    val epg = HttpEpgRepo()

    val todaysEpg = epg.epg()
    println(todaysEpg)

    val yesterdaysEpg = epg.epg(Calendar.getInstance().apply { roll(Calendar.DAY_OF_MONTH, false) })
    println(yesterdaysEpg)

    val someDayEpg = epg.epg(Calendar.getInstance().apply { set(2020, 10, 1) })
    println(someDayEpg)
}

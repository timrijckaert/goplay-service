package com.example.sample

import arrow.core.Tuple6
import be.tapped.vrtnu.content.ElasticSearchQueryBuilder
import be.tapped.vrtnu.content.LiveStreams
import be.tapped.vrtnu.content.VRTApi
import be.tapped.vrtnu.epg.HttpEpgRepo
import be.tapped.vrtnu.profile.*
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
    val allSearchResults = vrtApi.search(ElasticSearchQueryBuilder.SearchQuery(category = "cultuur")).toList()
    println(allSearchResults)

    // Single Program
    val programName = "merlina"
    val program = vrtApi.fetchProgramByName(programName).orNull()!!.program
    val playlist = vrtApi.fetchProgramPlaylist(program!!).orNull()!!
    println(program)
    println(playlist)

    // Fetch Video on Demand Streams
    val latestAiredEpisode = playlist.first().episodes.last()
    println(latestAiredEpisode)
    val latestAiredEpisodeStreamInfo = vrtApi.getVODStream(vrtPlayerToken, latestAiredEpisode.videoId, latestAiredEpisode.publicationId)
    println(latestAiredEpisodeStreamInfo)

    // Fetch Live Stream Video
    val vrtNWSLiveStreamInfo = vrtApi.getLiveStream(vrtPlayerToken, LiveStreams.een.videoId)
    println(vrtNWSLiveStreamInfo)
}

private suspend fun epg() {
    val epg = HttpEpgRepo()

    val todaysEpg = epg.epg()
    println(todaysEpg)

    val yesterdaysEpg = epg.epg(Calendar.getInstance().apply { roll(Calendar.DAY_OF_MONTH, false) })
    println(yesterdaysEpg)
}

package com.example.sample

import be.tapped.vtmgo.content.SearchResultType
import be.tapped.vtmgo.content.StoreFrontType
import be.tapped.vtmgo.content.TargetResponse
import be.tapped.vtmgo.content.VTMApi
import be.tapped.vtmgo.epg.HttpEpgRepo
import be.tapped.vtmgo.profile.HttpProfileRepo
import be.tapped.vtmgo.profile.JWT
import be.tapped.vtmgo.profile.Profile

public suspend fun main(args: Array<String>) {
    val userName = args[0]
    val password = args[1]

    // Authentication
    val (token, profile) = auth(userName, password)

    // Content
    api(token, profile)

    // EPG
    epg()
}

private suspend fun auth(userName: String, password: String): Pair<JWT, Profile> {
    val profileRepo = HttpProfileRepo()

    val jwtToken = profileRepo.login(userName, password)
    println(jwtToken)

    val token = jwtToken.orNull()!!.jwt
    val profiles = profileRepo.getProfiles(token)
    println("Fetched profiles=$profiles")

    val profile = profiles.orNull()!!.profiles.first()
    return token to profile
}

private suspend fun api(jwtToken: JWT, profile: Profile) {
    val vtmApi = VTMApi()

    // Live Channels
    val liveChannels = vtmApi.fetchChannels(jwtToken, profile)
    println(liveChannels)
    // Live Channel stream
    val firstLiveChannel = liveChannels.orNull()!!.channels.first()
    val firstLiveChannelStream = vtmApi.fetchStream(firstLiveChannel)
    println(firstLiveChannelStream)

    // Programs
    val catalogForChosenVtmGoProduct = vtmApi.fetchAZ(jwtToken, profile)
    val productTypeWithCatalog = catalogForChosenVtmGoProduct.orNull()!!.catalog.groupBy { it.target.asTarget::class.java }
    println(productTypeWithCatalog)
    //// Program Details
    val firstProgram = productTypeWithCatalog.getValue(TargetResponse.Target.Program::class.java).first()
    val programTarget = firstProgram.target.asTarget as TargetResponse.Target.Program
    val programDetailsForFirstProgram = vtmApi.fetchProgram(programTarget, jwtToken, profile)
    println(programDetailsForFirstProgram)
    //// Episode
    val streamsForFirstEpisodeOfFirstSeasonOfFirstProgram = vtmApi.fetchStream(TargetResponse.Target.Episode(programDetailsForFirstProgram.orNull()!!.program.seasons.first().episodes.first().id))
    println(streamsForFirstEpisodeOfFirstSeasonOfFirstProgram)

    // Categories
    val categories = vtmApi.fetchCategories(jwtToken, profile)
    println(categories)

    // Store front
    val series = vtmApi.fetchStoreFront(jwtToken, profile, StoreFrontType.MAIN)
    println(series)
    //// The Hunger Games: Catching Fire
    val theHungerGamesCatchingFire = TargetResponse.Target.Movie("2322020d-e6df-44fd-865c-a121b94f2e91")
    val theHungerGamesCatchingFireStream = vtmApi.fetchStream(theHungerGamesCatchingFire)
    println(theHungerGamesCatchingFireStream)

    // Favorites
    val myFavorites = vtmApi.fetchMyFavorites(jwtToken, profile)
    println(myFavorites)

    // Search
    val searchResult = vtmApi.search(jwtToken, profile, "Code van Coppens")
    val firstExactSearchResultProgramTarget = (searchResult.orNull()!!.search.first { it.type == SearchResultType.EXACT }.teasers.first().target.asTarget as TargetResponse.Target.Program)
    val firstExactProgram = vtmApi.fetchProgram(firstExactSearchResultProgramTarget, jwtToken, profile)
    val streamOfActiveEpisodeOfSearchedProgram = vtmApi.fetchStream(TargetResponse.Target.Episode(firstExactProgram.orNull()!!.program.seasons.first().episodes.first().id))
    println(streamOfActiveEpisodeOfSearchedProgram)
}

private suspend fun epg() {
    val httpEpgRepo = HttpEpgRepo()
    val todayEpg = httpEpgRepo.epg()
    println(todayEpg)
}

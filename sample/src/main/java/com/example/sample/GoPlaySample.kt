package com.example.sample

import arrow.core.Tuple2
import arrow.core.toT
import arrow.fx.coroutines.parTraverse
import be.tapped.goplay.ApiResponse
import be.tapped.goplay.content.EpisodeUuid
import be.tapped.goplay.content.GoPlayApi
import be.tapped.goplay.content.Program
import be.tapped.goplay.content.SearchHit
import be.tapped.goplay.epg.EpgRepo
import be.tapped.goplay.epg.HttpEpgRepo
import be.tapped.goplay.profile.HttpProfileRepo

public suspend fun main(args: Array<String>) {
    val userName = args[0]
    val password = args[1]

    val (token, profile) = authentication(userName, password)
    println(token)
    println(profile)

    api(token)

    epg()
}

private suspend fun api(token: ApiResponse.Success.Authentication.Token) {
    val vierApi = GoPlayApi()

    // All Programs
    val fetchPrograms = vierApi.fetchPrograms()
    val programs = fetchPrograms.orNull()!!
    println(programs)

    val episodesOfFirstProgram: List<Program.Playlist.Episode> = programs.programs.first().playlists.flatMap(Program.Playlist::episodes)
    println(episodesOfFirstProgram)
    val episodeVideoUuids = episodesOfFirstProgram.map(Program.Playlist.Episode::id)
    println(episodeVideoUuids)

    val episodeStreams = episodeVideoUuids.parTraverse { vierApi.streamByVideoUuid(token.token.idToken, it) }
    println(episodeStreams)

    // Program by URL
    val deSlimsteMensTerWereldByProgramUrl =
            vierApi.fetchProgram(SearchHit.Source.SearchKey.Program("https://www.goplay.be/de-slimste-mens-ter-wereld"))
    println(deSlimsteMensTerWereldByProgramUrl)

    // Search
    val searchResults = vierApi.search("big brother")
    println(searchResults)

    if (searchResults.isRight()) {
        searchResults.orNull()!!.hits
                .filter { it.source.bundle == SearchHit.Source.Bundle.PROGRAM || it.source.bundle == SearchHit.Source.Bundle.VIDEO }
                .groupBy { it.source.bundle }
                .forEach { (bundle, searchHits) ->
                    if (bundle == SearchHit.Source.Bundle.PROGRAM) {
                        val programsFromSearchHits =
                                searchHits
                                        .map { it.source.searchKey as SearchHit.Source.SearchKey.Program }
                                        .parTraverse(vierApi::fetchProgram)
                        println(programsFromSearchHits)
                    }
                    if (bundle == SearchHit.Source.Bundle.VIDEO) {
                        val episodesFromSearchHits =
                                searchHits
                                        .map { it.source.searchKey as SearchHit.Source.SearchKey.EpisodeByNodeId }
                                        .parTraverse(vierApi::fetchEpisode)
                        println(episodesFromSearchHits)
                    }
                }
    }

    // Episode by Episode UUID
    val episode = vierApi.fetchEpisode(EpisodeUuid("c4e8d653-224a-4985-95ac-c8360074c518"))
    println(episode)
}

private suspend fun authentication(
        userName: String,
        password: String,
): Tuple2<ApiResponse.Success.Authentication.Token, ApiResponse.Success.Authentication.Profile> {
    val httpProfileRepo = HttpProfileRepo()
    val token = httpProfileRepo.fetchTokens(userName, password).orNull()!!
    // Assert that the new tokens are able to be fetched
    httpProfileRepo.refreshTokens(token.token.refreshToken).orNull()!!

    val profile = httpProfileRepo.getUserAttributes(token.token.accessToken).orNull()!!
    return token toT profile
}

private suspend fun epg() {
    val epgRepo = HttpEpgRepo()
    val todayEpgVier = epgRepo.epg(EpgRepo.Brand.VIER)
    val todayEpgVijf = epgRepo.epg(EpgRepo.Brand.VIJF)
    val todayEpgZes = epgRepo.epg(EpgRepo.Brand.ZES)
    println(todayEpgVier)
    println(todayEpgVijf)
    println(todayEpgZes)
}

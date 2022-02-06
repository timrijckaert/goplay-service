package be.tapped.goplay.content

import be.tapped.goplay.common.goPlayApiDefaultOkHttpClient
import be.tapped.goplay.common.jsonSerializer
import be.tapped.goplay.epg.EpgRepo
import be.tapped.goplay.epg.defaultEpgRepo

public object GoPlayApi :
    ProgramRepo by HttpProgramRepo(
        goPlayApiDefaultOkHttpClient,
        HtmlProgramParser(JsoupParser(), jsonSerializer),
        HtmlFullProgramParser(JsoupParser(), jsonSerializer),
        ProgramResponseValidator()
    ),
    EpisodeRepo by HttpEpisodeRepo(
        goPlayApiDefaultOkHttpClient,
        HtmlFullProgramParser(JsoupParser(), jsonSerializer),
        HtmlClipEpisodeParser(JsoupParser()),
        EpisodeParser()
    ),
    StreamRepo by HttpStreamRepo(goPlayApiDefaultOkHttpClient, JsonStreamParser()),
    SearchRepo by HttpSearchRepo(goPlayApiDefaultOkHttpClient, JsonSearchResultsParser()),
    EpgRepo by defaultEpgRepo()

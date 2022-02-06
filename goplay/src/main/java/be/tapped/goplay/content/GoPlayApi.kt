package be.tapped.goplay.content

import be.tapped.goplay.common.goPlayApiDefaultOkHttpClient

public object GoPlayApi :
    ProgramRepo by HttpProgramRepo(
        goPlayApiDefaultOkHttpClient,
        HtmlProgramParser(JsoupParser()),
        HtmlFullProgramParser(JsoupParser()),
        ProgramResponseValidator()
    ),
    EpisodeRepo by HttpEpisodeRepo(
        goPlayApiDefaultOkHttpClient,
        HtmlFullProgramParser(JsoupParser()),
        HtmlClipEpisodeParser(JsoupParser()),
        EpisodeParser()
    ),
    StreamRepo by HttpStreamRepo(goPlayApiDefaultOkHttpClient, JsonStreamParser()),
    SearchRepo by HttpSearchRepo(goPlayApiDefaultOkHttpClient, JsonSearchResultsParser())

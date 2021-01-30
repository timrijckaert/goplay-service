package be.tapped.goplay.content

import be.tapped.goplay.common.goPlayApiDefaultOkHttpClient

public class GoPlayApi(
        private val programRepo: ProgramRepo = HttpProgramRepo(
                goPlayApiDefaultOkHttpClient,
                HtmlProgramParser(JsoupParser()),
                HtmlFullProgramParser(JsoupParser()),
                ProgramResponseValidator()
        ),
        episodeRepo: EpisodeRepo = HttpEpisodeRepo(
                goPlayApiDefaultOkHttpClient,
                HtmlFullProgramParser(JsoupParser()),
                HtmlClipEpisodeParser(JsoupParser()),
                EpisodeParser()
        ),
        private val streamRepo: StreamRepo = HttpStreamRepo(goPlayApiDefaultOkHttpClient, JsonStreamParser()),
        private val searchRepo: SearchRepo = HttpSearchRepo(goPlayApiDefaultOkHttpClient, JsonSearchResultsParser()),
) :
        ProgramRepo by programRepo,
        EpisodeRepo by episodeRepo,
        StreamRepo by streamRepo,
        SearchRepo by searchRepo

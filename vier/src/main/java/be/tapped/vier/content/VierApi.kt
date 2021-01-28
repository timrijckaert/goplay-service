package be.tapped.vier.content

import be.tapped.vier.common.vierApiDefaultOkHttpClient

public class VierApi(
    private val programRepo: ProgramRepo = HttpProgramRepo(
        vierApiDefaultOkHttpClient, HtmlProgramParser(JsoupParser()), HtmlFullProgramParser(JsoupParser())
    ),
    episodeRepo: EpisodeRepo = HttpEpisodeRepo(
        vierApiDefaultOkHttpClient, HtmlFullProgramParser(JsoupParser()), HtmlClipEpisodeParser(JsoupParser()), EpisodeParser()
    ),
    private val streamRepo: StreamRepo = HttpStreamRepo(vierApiDefaultOkHttpClient, JsonStreamParser()),
    private val searchRepo: SearchRepo = HttpSearchRepo(vierApiDefaultOkHttpClient, JsonSearchResultsParser()),
) : ProgramRepo by programRepo, EpisodeRepo by episodeRepo, StreamRepo by streamRepo, SearchRepo by searchRepo

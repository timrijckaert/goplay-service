package be.tapped.vier.content

import be.tapped.vier.common.vierApiDefaultOkHttpClient

public class VierApi(
    private val programRepo: ProgramRepo = HttpProgramRepo(
        vierApiDefaultOkHttpClient,
        HtmlPartialProgramParser(),
        HtmlProgramParser(),
    ),
    private val streamRepo: StreamRepo = HttpStreamRepo(vierApiDefaultOkHttpClient, JsonStreamParser()),
    private val searchRepo: SearchRepo = HttpSearchRepo(vierApiDefaultOkHttpClient, JsonSearchResultsParser()),
) : ProgramRepo by programRepo,
    StreamRepo by streamRepo,
    SearchRepo by searchRepo

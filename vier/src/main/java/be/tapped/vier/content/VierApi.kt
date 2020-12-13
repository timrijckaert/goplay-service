package be.tapped.vier.content

import be.tapped.vier.common.vierApiDefaultOkHttpClient

public class VierApi(
    private val programRepo: ProgramRepo = HttpProgramRepo(
        vierApiDefaultOkHttpClient,
        HtmlSimpleProgramParser(),
        HtmlProgramParser(),
    ),
    private val streamRepo: StreamRepo = HttpStreamRepo(vierApiDefaultOkHttpClient, JsonStreamParser()),
) : ProgramRepo by programRepo,
    StreamRepo by streamRepo

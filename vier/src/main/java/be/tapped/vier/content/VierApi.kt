package be.tapped.vier.content

import be.tapped.vier.common.vierApiDefaultOkHttpClient

public class VierApi(
    private val programRepo: ProgramRepo = HttpProgramRepo(
        vierApiDefaultOkHttpClient,
        HtmlSimpleProgramParser(),
        HtmlProgramParser(),
    ),
) : ProgramRepo by programRepo

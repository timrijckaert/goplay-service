package be.tapped.vier.content

import arrow.core.Either
import arrow.core.computations.either
import be.tapped.common.internal.executeAsync
import be.tapped.vier.ApiResponse
import be.tapped.vier.common.safeBodyString
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

internal class HtmlProgramParser {
    fun parse(document: Document): List<Program> {
        val elements = document.select("a.program-overview__link")
        elements.forEach {
            val path = it.attr("href")
            val title = it.child(0).text()
            println(path)
            println(title)
        }
        return emptyList()
    }
}

public interface ProgramRepo {
    public suspend fun fetchPrograms(): Either<ApiResponse.Failure, ApiResponse.Success.Content.Programs>
}

internal class HttpProgramRepo(
    private val client: OkHttpClient,
    private val htmlProgramParser: HtmlProgramParser = HtmlProgramParser(),
) : ProgramRepo {
    override suspend fun fetchPrograms(): Either<ApiResponse.Failure, ApiResponse.Success.Content.Programs> {
        return either {
            val html = !client.executeAsync(
                Request.Builder()
                    .get()
                    .url("https://www.vier.be")
                    .build()
            ).safeBodyString()

            val document: Document = Jsoup.parse(html)
            ApiResponse.Success.Content.Programs(htmlProgramParser.parse(document))
        }
    }
}

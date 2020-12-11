package be.tapped.vier.content

import arrow.core.*
import arrow.core.computations.either
import arrow.core.extensions.Tuple2Semigroup
import arrow.core.extensions.applicativeNel
import arrow.core.extensions.either.monad.flatMap
import arrow.core.extensions.validated.applicative.applicative
import be.tapped.common.internal.executeAsync
import be.tapped.vier.ApiResponse
import be.tapped.vier.common.safeBodyString
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

public data class SimpleProgram(public val name: String, public val path: String)

internal class HtmlProgramParser {

    private fun Element.attribute(attributeKey: String): Validated<ApiResponse.Failure.HTML.MissingAttributeValue, String> {
        val attr = attr(attributeKey)
        return if (attr.isEmpty()) {
            ApiResponse.Failure.HTML.MissingAttributeValue(attributeKey).invalid()
        } else {
            attr.valid()
        }
    }

    private suspend fun Element.safeChild(index : Int) : Validated<ApiResponse.Failure.HTML.NoChildAtPosition, Element> =
        Validated.catch { child(index) }.mapLeft { ApiResponse.Failure.HTML.NoChildAtPosition(index) }

    private fun Element.safeText() : Either<ApiResponse.Failure.HTML.EmptyHTML, String> {
        val text = text()
        return Either.conditionally(text.isNotBlank(), ifFalse = { ApiResponse.Failure.HTML.EmptyHTML }, ifTrue = { text })
    }

    private fun Document.safeSelect(cssQuery: String): Either<ApiResponse.Failure.HTML.NoChildren, Elements> {
        val elements = select(cssQuery)
        return if (elements.isEmpty()) {
            ApiResponse.Failure.HTML.NoChildren(cssQuery).left()
        } else {
            elements.right()
        }
    }

    suspend fun parse(document: Document): List<SimpleProgram> {
        val a = either {
            val elements = !document.safeSelect("a.program-overview__link")
            elements.map {
                val path = it.attribute("href")
                val title = it.safeChild(0).withEither { it.flatMap { it.safeText() } }

            }
            ""
        }
        //return elements.map {
        //    val path = it.attr("href").valid()
        //    val title = it.child(0).text()
        //    SimpleProgram(title, path)
        //}
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

            val htmlDocument = Jsoup.parse(html)
            val simplePrograms = htmlProgramParser.parse(htmlDocument)
            ApiResponse.Success.Content.Programs(emptyList())
        }
    }
}

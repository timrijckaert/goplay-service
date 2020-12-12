package be.tapped.vier.content

import arrow.core.*
import arrow.core.computations.either
import arrow.core.extensions.nonemptylist.semigroup.semigroup
import arrow.core.extensions.validated.applicative.applicative
import arrow.core.extensions.validated.functor.map
import be.tapped.common.internal.executeAsync
import be.tapped.vier.ApiResponse
import be.tapped.vier.common.safeBodyString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
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

    private suspend fun Element.safeChild(index: Int): Validated<ApiResponse.Failure.HTML.NoChildAtPosition, Element> =
        Validated.catch { child(index) }.mapLeft { ApiResponse.Failure.HTML.NoChildAtPosition(index, childrenSize()) }

    private fun Element.safeText(): Either<ApiResponse.Failure.HTML.EmptyHTML, String> {
        val text = text()
        return Either.conditionally(
            text.isNotBlank(),
            ifFalse = { ApiResponse.Failure.HTML.EmptyHTML },
            ifTrue = { text }
        )
    }

    private fun Document.safeSelect(cssQuery: String): Either<ApiResponse.Failure.HTML.NoChildren, Elements> {
        val elements = select(cssQuery)
        return if (elements.isEmpty()) {
            ApiResponse.Failure.HTML.NoChildren(cssQuery).left()
        } else {
            elements.right()
        }
    }

    suspend fun parse(document: Document): Flow<Either<ApiResponse.Failure.HTML, SimpleProgram>> =
        flow {
            when (val links = document.safeSelect("a.program-overview__link")) {
                is Either.Left -> emit(links)
                is Either.Right -> {
                    links.b.forEach { link ->
                        val path = link.attribute("href").toValidatedNel()
                        val title = link.safeChild(0).withEither { it.flatMap { it.safeText() } }.toValidatedNel()
                        when (val program = Validated
                            .applicative(NonEmptyList.semigroup<ApiResponse.Failure.HTML>())
                            .tupledN(title, path)
                            .map { (title, path) -> SimpleProgram(title, path) }
                            .mapLeft { ApiResponse.Failure.HTML.Parsing(it) }
                            .fix()
                        ) {
                            is Validated.Valid   -> emit(program.a.right())
                            is Validated.Invalid -> emit(program.e.left())
                        }
                    }
                }
            }
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
            val simplePrograms = htmlProgramParser.parse(htmlDocument).toList()
            ApiResponse.Success.Content.Programs(emptyList())
        }
    }
}

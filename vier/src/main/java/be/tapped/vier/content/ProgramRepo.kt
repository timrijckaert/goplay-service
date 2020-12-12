package be.tapped.vier.content

import arrow.Kind
import arrow.core.*
import arrow.core.computations.either
import arrow.core.extensions.list.traverse.sequence
import arrow.core.extensions.nonemptylist.semigroup.semigroup
import arrow.core.extensions.validated.applicative.applicative
import arrow.core.extensions.validated.bifunctor.mapLeft
import arrow.core.extensions.validated.functor.map
import be.tapped.common.internal.executeAsync
import be.tapped.common.internal.toValidateNel
import be.tapped.vier.ApiResponse
import be.tapped.vier.ApiResponse.*
import be.tapped.vier.ApiResponse.Failure.*
import be.tapped.vier.ApiResponse.Failure.HTML.*
import be.tapped.vier.common.safeBodyString
import kotlinx.coroutines.flow.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

public data class SimpleProgram(public val name: String, public val path: String)

internal class HtmlProgramParser {

    private fun Element.attribute(attributeKey: String): Validated<MissingAttributeValue, String> {
        val attr = attr(attributeKey)
        return if (attr.isEmpty()) {
            MissingAttributeValue("$this", attributeKey).invalid()
        } else {
            attr.valid()
        }
    }

    private suspend fun Element.safeChild(index: Int): Either<NoChildAtPosition, Element> =
        Either.catch { child(index) }.mapLeft { NoChildAtPosition("$this", index, childrenSize()) }

    private fun Element.safeText(): Either<EmptyHTML, String> {
        val text = text()
        return Either.conditionally(
            text.isNotBlank(),
            ifFalse = { EmptyHTML },
            ifTrue = { text }
        )
    }

    private fun Document.safeSelect(cssQuery: String): Either<NoSelection, Elements> {
        val elements = select(cssQuery)
        return if (elements.isEmpty()) {
            NoSelection("$this", cssQuery).left()
        } else {
            elements.right()
        }
    }

    private val applicative = Validated
        .applicative(NonEmptyList.semigroup<HTML>())

    suspend fun parse(document: Document): Either<HTML, List<SimpleProgram>> =
        document.safeSelect("a.program-overview__link").flatMap { links ->
            links.map { link ->
                val path = link.attribute("href").toValidatedNel()
                val title = link.safeChild(0).flatMap { it.safeText() }.toValidateNel()
                applicative.mapN(title, path) { (title, path) -> SimpleProgram(title, path) }
            }.sequence(applicative)
                .mapLeft(::Parsing)
                .map(Kind<ForListK, SimpleProgram>::fix)
                .toEither()
        }
}

public interface ProgramRepo {
    public suspend fun fetchPrograms(): Either<Failure, Success.Content.Programs>
}

internal class HttpProgramRepo(
    private val client: OkHttpClient,
    private val htmlProgramParser: HtmlProgramParser = HtmlProgramParser(),
) : ProgramRepo {
    override suspend fun fetchPrograms(): Either<Failure, Success.Content.Programs> {
        return either {
            val html = !client.executeAsync(
                Request.Builder()
                    .get()
                    .url("https://www.vier.be")
                    .build()
            ).safeBodyString()

            val htmlDocument = Jsoup.parse(html)
            val simplePrograms = htmlProgramParser.parse(htmlDocument)
            Success.Content.Programs(emptyList())
        }
    }
}

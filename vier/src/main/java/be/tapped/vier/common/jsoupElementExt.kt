package be.tapped.vier.common

import arrow.core.Either
import arrow.core.Validated
import arrow.core.invalid
import arrow.core.left
import arrow.core.right
import arrow.core.valid
import be.tapped.vier.ApiResponse
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

internal fun Element.safeAttr(attributeKey: String): Validated<ApiResponse.Failure.HTML.MissingAttributeValue, String> {
    val attr = attr(attributeKey)
    return if (attr.isEmpty()) {
        ApiResponse.Failure.HTML.MissingAttributeValue(attributeKey).invalid()
    } else {
        attr.valid()
    }
}

internal suspend fun Element.safeChild(index: Int): Either<ApiResponse.Failure.HTML.NoChildAtPosition, Element> =
    Either.catch { child(index) }.mapLeft { ApiResponse.Failure.HTML.NoChildAtPosition(index, childrenSize()) }

internal fun Element.safeText(): Either<ApiResponse.Failure.HTML.EmptyHTML, String> {
    val text = text()
    return Either.conditionally(text.isNotBlank(), ifFalse = { ApiResponse.Failure.HTML.EmptyHTML }, ifTrue = { text })
}

internal fun Element.safeSelect(cssQuery: String): Either<ApiResponse.Failure.HTML.NoSelection, Elements> {
    val elements = select(cssQuery)
    return if (elements.isEmpty()) {
        ApiResponse.Failure.HTML.NoSelection(cssQuery).left()
    } else {
        elements.right()
    }
}

internal fun Element.safeSelectFirst(cssQuery: String): Either<ApiResponse.Failure.HTML.NoSelection, Element> =
    selectFirst(cssQuery)?.right() ?: ApiResponse.Failure.HTML.NoSelection(cssQuery).left()

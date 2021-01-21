package be.tapped.vrtnu.content

internal class UrlPrefixMapper {
    fun toHttpsUrl(incompleteUrl: String): String = if (incompleteUrl.isBlank()) incompleteUrl else "https:${incompleteUrl}"
}

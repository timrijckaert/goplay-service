package be.tapped.vrtnu.content

internal class UrlPrefixMapper {
    fun toHttpsUrl(incompleteUrl: String) = "https:${incompleteUrl}"
}

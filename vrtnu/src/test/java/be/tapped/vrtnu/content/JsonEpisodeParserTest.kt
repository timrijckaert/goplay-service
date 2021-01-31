package be.tapped.vrtnu.content

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

public class JsonEpisodeParserTest : StringSpec({

    "should be able to parse the episodes" {
        val episodeJson = javaClass.classLoader?.getResourceAsStream("episodes.json")!!.reader().readText()
        val searchResult = JsonSearchHitParser(UrlPrefixMapper()).parse(episodeJson).orNull()!!
        searchResult.results shouldHaveSize 22
        searchResult.meta shouldBe Meta(22, Pages(1, 1, 150))
        searchResult.facets shouldBe FacetWrapper(
                listOf(
                        Facet("seasons", listOf(Bucket("2020", 22))),
                        Facet("brands", listOf(Bucket("canvas", 22))),
                        Facet("programs", listOf(Bucket("Terzake", 22))),
                        Facet("categories", listOf(Bucket("nieuws-en-actua", 22), Bucket("talkshows", 22))),
                )
        )
    }

})

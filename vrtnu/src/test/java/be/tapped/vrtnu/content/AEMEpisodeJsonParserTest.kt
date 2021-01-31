package be.tapped.vrtnu.content

import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize

public class AEMEpisodeJsonParserTest : StringSpec({

    "should be able to parse the seasons of 'het journaal'" {
        val aemJcrContentString = javaClass.classLoader?.getResourceAsStream("het-journaal-jcr-content.json")!!.reader().readText()
        val seasons = AEMEpisodeJsonParser(UrlPrefixMapper()).parse(aemJcrContentString)
        assertSoftly {
            seasons.shouldBeRight()
            seasons.orNull()!! shouldHaveSize 1
        }
    }

    "should be able to parse the seasons of 'merlina'" {
        val aemJcrContentString = javaClass.classLoader?.getResourceAsStream("merlina-jcr-content.json")!!.reader().readText()
        val seasons = AEMEpisodeJsonParser(UrlPrefixMapper()).parse(aemJcrContentString)
        assertSoftly {
            seasons.shouldBeRight()
            seasons.orNull()!! shouldHaveSize 4
        }
    }
})

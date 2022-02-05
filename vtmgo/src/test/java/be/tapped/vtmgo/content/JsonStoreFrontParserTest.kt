package be.tapped.vtmgo.content

import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize

public class JsonStoreFrontParserTest : StringSpec({
    "Should be able to parse" {
        val storeFrontDetails = javaClass.classLoader?.getResourceAsStream("store-front.json")!!.reader().readText()
        val storeFront = JsonStoreFrontParser().parseListOfStoreFront(storeFrontDetails)
        storeFront.shouldBeRight()
    }
})

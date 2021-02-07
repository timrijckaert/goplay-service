package be.tapped.vtmgo.content

import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

public class JsonFavoritesParserTest : StringSpec({

    "should be able to parse your favourites" {
        val favoritesJson = javaClass.classLoader?.getResourceAsStream("favorites.json")!!.reader().readText()
        val sut = JsonFavoritesParser()
        val favorites = sut.parse(favoritesJson)
        favorites.shouldBeRight()
    }
})

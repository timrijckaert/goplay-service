package be.tapped.vtmgo.content

import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.StringSpec

public class JsonFavoritesParserTest : StringSpec({

    "should be able to parse your favourites" {
        val favoritesJson = javaClass.classLoader?.getResourceAsStream("favorites.json")!!.reader().readText()
        val favorites = JsonFavoritesParser().parse(favoritesJson)
        favorites.shouldBeRight()
    }
})

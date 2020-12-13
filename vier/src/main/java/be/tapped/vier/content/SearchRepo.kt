package be.tapped.vier.content

import arrow.core.Either
import be.tapped.common.internal.executeAsync
import be.tapped.vier.ApiResponse
import be.tapped.vier.common.safeBodyString
import be.tapped.vier.common.vierBaseApiUrl
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

public interface SearchRepo {
    public suspend fun search(query: String): Either<ApiResponse.Failure, ApiResponse.Success.Content.Search>
}

internal class HttpSearchRepo(private val client: OkHttpClient) : SearchRepo {

    // curl -X POST \
    // -H  -d '{ "query": <query>,"sites":["vier"],"page":0,"mode":"byDate"}' "https://api.viervijfzes.be/search"
    override suspend fun search(query: String): Either<ApiResponse.Failure, ApiResponse.Success.Content.Search> {
        val searchResponse = client.executeAsync(
            Request.Builder()
                .post(
                    buildJsonObject {
                        put("query", query)
                        put("sites", buildJsonArray {
                            add("vier")
                            // TODO add ability to search in vijf and zes
                            // add("vijf")
                            // add("zes")
                        })
                        put("page", 0)
                        put("mode", "byDate")
                    }.toString().toRequestBody()
                )
                .url("$vierBaseApiUrl/search")
                .build()
        )
        val searchJson = searchResponse.safeBodyString()
        TODO()
    }
}

// {
//     "took": 8,
//     "timed_out": false,
//     "_shards": {
//     "total": 1,
//     "successful": 1,
//     "failed": 0
// },
//     "hits": {
//     "total": 7523,
//     "max_score": 19.209963,
//     "hits": [
//     {
//         "_index": "production",
//         "_type": "page",
//         "_id": "vier:node:en:31097",
//         "_score": 19.209963,
//         "_source": {
//         "id": "31097",
//         "type": "node",
//         "bundle": "program",
//         "url": "https://www.vier.be/de-slimste-mens-in-huis",
//         "language": "en",
//         "title": "De Slimste Mens in Huis",
//         "site": "vier",
//         "intro": "Erik Van Looy presenteert vanuit zijn eigen kot 'De Slimste Mens van het Huis', een miniquiz voor bij jou thuis. ",
//         "created": 1607814035,
//         "changed": 1587151694,
//         "body": [],
//         "terms": [
//         "erik van looy",
//         "De Slimste Mens ter Wereld"
//         ],
//         "suggest": "De Slimste Mens in Huis",
//         "program": "",
//         "img": "https://images.viervijfzes.be/www.vier.be/production/meta/tnepg3840x2160slimstemensinhuis-q7v127.jpg?auto=format&fit=crop&h=452&ixlib=php-1.1.0&q=65&w=682&s=4e84a4c11f015f3fafb217c0be8c8495"
//     },
//         "highlight": {
//         "intro": [
//         "Erik Van Looy presenteert vanuit zijn eigen kot '<em>De</em> <em>Slimste</em> Mens van het Huis', een miniquiz voor bij jou thuis. "
//         ],
//         "title": [
//         "<em>De</em> <em>Slimste</em> Mens in Huis"
//         ]
//     }
//     },
//     {
//         "_index": "production",
//         "_type": "page",
//         "_id": "vier:node:en:189",
//         "_score": 19.209885,
//         "_source": {
//         "id": "189",
//         "type": "node",
//         "bundle": "program",
//         "url": "https://www.vier.be/de-slimste-mens-ter-wereld",
//         "language": "en",
//         "title": "De Slimste Mens ter Wereld",
//         "site": "vier",
//         "intro": "Het nieuwe seizoen van De Slimste Mens ter Wereld start op 12 oktober! ",
//         "created": 1607814030,
//         "changed": 1602569960,
//         "body": [
//         "<p>De leukste quiz ter wereld brengt&nbsp;oneindig veel weetjes, de grappigste filmpjes en een flinke portie spanning naar de Vlaamse huiskamers. Dat alles met de steun van een ontzettend toegewijde jury.</p>\r\n"
//         ],
//         "terms": [
//         "De Slimste Mens ter Wereld",
//         "Quiz",
//         "erik van looy",
//         "Jeroom",
//         "Maaike Cafmeyer",
//         "Bart Cannaerts",
//         "Gilles Van Bouwel",
//         "Philippe Geubels",
//         "Marc-Marie Huijbregts",
//         "Sam Gooris",
//         "jonas geirnaert",
//         "Jan-Jaap Van der Wal",
//         "Herman Brusselmans",
//         "Karen Damen",
//         "Stefaan Degand",
//         "Wim Helsen"
//         ],
//         "suggest": "De Slimste Mens ter Wereld",
//         "program": "",
//         "img": "https://images.viervijfzes.be/www.vier.be/production/meta/dsmtws183840x2160wallpaper-qh3sr2.jpg?auto=format&fit=crop&h=452&ixlib=php-1.1.0&q=65&w=682&s=559b7c2ae29aa7b0cd433a4eb3a772cd"
//     },
//         "highlight": {
//         "intro": [
//         "Het nieuwe seizoen van <em>De</em> <em>Slimste</em> Mens ter Wereld start op 12 oktober! "
//         ],
//         "title": [
//         "<em>De</em> <em>Slimste</em> Mens ter Wereld"
//         ],
//         "body": [
//         "<p><em>De</em> leukste quiz ter wereld brengt&nbsp;oneindig veel weetjes, <em>de</em> grappigste filmpjes en een flinke portie spanning"
//         ]
//     }
//     },
//     {
//         "_index": "production",
//         "_type": "page",
//         "_id": "vier:node:en:35399",
//         "_score": 11.734189,
//         "_source": {
//         "id": "35399",
//         "type": "node",
//         "bundle": "video",
//         "url": "https://www.vier.be/video/de-slimste-mens-ter-wereld/de-slimste-mens-ter-wereld-s18/de-slimste-mens-ter-wereld-s18-aflevering-36",
//         "language": "en",
//         "title": "De Slimste Mens Ter Wereld - S18 - Aflevering 36",
//         "site": "vier",
//         "intro": "Erik Van Looy gaat in het 18e seizoen opnieuw op zoek naar De Slimste Mens ter Wereld in het 18e seizoen van de populaire quiz met Ella Leyers, Wesley Sonck, Pedro Elias, Liesbeth Van Impe, Ben Crabbé",
//         "created": 1607632980,
//         "changed": 1607632980,
//         "body": [],
//         "terms": [
//         "De Slimste Mens ter Wereld",
//         "Volledige Aflevering"
//         ],
//         "suggest": "De Slimste Mens Ter Wereld - S18 - Aflevering 36",
//         "program": "De Slimste Mens ter Wereld",
//         "img": "https://images.viervijfzes.be/www.vier.be/production/meta/36-ql43js.png?auto=format&fit=crop&h=452&ixlib=php-1.1.0&q=65&w=682&s=055d7bc7a7ce3c69bb1251d629ed9261",
//         "duration": 3414
//     },
//         "highlight": {
//         "intro": [
//         "Erik Van Looy gaat in het 18e seizoen opnieuw op zoek naar <em>De</em> <em>Slimste</em> Mens ter Wereld in het 18e seizoen van <em>de</em>"
//         ],
//         "title": [
//         "<em>De</em> <em>Slimste</em> Mens Ter Wereld - S18 - Aflevering 36"
//         ]
//     }
//     },
//     {
//         "_index": "production",
//         "_type": "page",
//         "_id": "vier:node:en:35377",
//         "_score": 11.123859,
//         "_source": {
//         "id": "35377",
//         "type": "node",
//         "bundle": "stub",
//         "url": "https://www.vier.be/de-slimste-mens-ter-wereld/win-de-slimste-scheurkalender-ter-wereld-2021",
//         "language": "en",
//         "title": "Win de Slimste Scheurkalender ter Wereld 2021",
//         "site": "vier",
//         "intro": "Wie weet dat Tinder niet de beste plaats is voor het vinden van het lief van je dromen, maar ook dat je bij het rijden van een scheve schaats erg ongelukkig ten val kan komen?",
//         "created": 1607506076,
//         "changed": 1607506163,
//         "body": [],
//         "terms": [],
//         "suggest": "Win de Slimste Scheurkalender ter Wereld 2021",
//         "program": "De Slimste Mens ter Wereld",
//         "img": "https://images.viervijfzes.be/www.vier.be/production/meta/schermafbeelding-2020-12-08-om-132536-ql2fo6.png?auto=format&fit=crop&h=452&ixlib=php-1.1.0&q=65&w=682&s=2fb631080d713d9f70fb4b1daf98f3df"
//     },
//         "highlight": {
//         "intro": [
//         "Wie weet dat Tinder niet <em>de</em> beste plaats is voor het vinden van het lief van je dromen, maar ook dat je bij het rijden van een scheve schaats erg ongelukkig ten val kan komen?"
//         ],
//         "title": [
//         "Win <em>de</em> <em>Slimste</em> Scheurkalender ter Wereld 2021"
//         ]
//     }
//     },
//     {
//         "_index": "production",
//         "_type": "page",
//         "_id": "vier:node:en:35373",
//         "_score": 10.95313,
//         "_source": {
//         "id": "35373",
//         "type": "node",
//         "bundle": "video",
//         "url": "https://www.vier.be/video/de-slimste-mens-ter-wereld/de-slimste-mens-ter-wereld-s18/de-slimste-mens-ter-wereld-s18-aflevering-35",
//         "language": "en",
//         "title": "De Slimste Mens Ter Wereld - S18 - Aflevering 35",
//         "site": "vier",
//         "intro": "Erik Van Looy gaat in het 18e seizoen opnieuw op zoek naar De Slimste Mens ter Wereld in het 18e seizoen van de populaire quiz met Ella Leyers, Wesley Sonck, Pedro Elias, Liesbeth Van Impe, Ben Crabbé",
//         "created": 1607547120,
//         "changed": 1607547120,
//         "body": [],
//         "terms": [
//         "De Slimste Mens ter Wereld",
//         "Volledige Aflevering"
//         ],
//         "suggest": "De Slimste Mens Ter Wereld - S18 - Aflevering 35",
//         "program": "De Slimste Mens ter Wereld",
//         "img": "https://images.viervijfzes.be/www.vier.be/production/meta/35-ql2ada.png?auto=format&fit=crop&h=452&ixlib=php-1.1.0&q=65&w=682&s=03c83fcb6d543a655398006dcfaeb2eb",
//         "duration": 3328
//     },
//         "highlight": {
//         "intro": [
//         "Erik Van Looy gaat in het 18e seizoen opnieuw op zoek naar <em>De</em> <em>Slimste</em> Mens ter Wereld in het 18e seizoen van <em>de</em>"
//         ],
//         "title": [
//         "<em>De</em> <em>Slimste</em> Mens Ter Wereld - S18 - Aflevering 35"
//         ]
//     }
//     },
//     {
//         "_index": "production",
//         "_type": "page",
//         "_id": "vier:node:en:35365",
//         "_score": 10.4985695,
//         "_source": {
//         "id": "35365",
//         "type": "node",
//         "bundle": "article",
//         "url": "https://www.vier.be/de-slimste-mens-ter-wereld/win-de-slimste-scheurkalender-ter-wereld-2021",
//         "language": "en",
//         "title": "Maak kans op de Slimste Scheurkalender ter Wereld 2021",
//         "site": "vier",
//         "intro": "Wie weet dat Tinder niet de beste plaats is voor het vinden van het lief van je dromen, maar ook dat je bij het rijden van een scheve schaats erg ongelukkig ten val kan komen?",
//         "created": 1607494286,
//         "changed": 1607508658,
//         "body": [
//         "<p>Wie weet dat Tinder niet de beste plaats is voor het vinden van het lief van je dromen, maar ook dat je bij het rijden van een scheve schaats erg ongelukkig ten val kan komen? Wie weet dat om een kater te voorkomen je best blijft drinken, dat eenjarige runderen geen ‘kalveren’ meer zijn maar ‘pinken’, maar ook dat de reuzenaronskelk 72 uur per jaar enorm kan stinken?</p>\r\n\r\n<h3><strong>Lees het in de Slimste Scheurkalender ter Wereld 2021. </strong></h3>\r\n"
//         ],
//         "terms": [],
//         "suggest": "Maak kans op de Slimste Scheurkalender ter Wereld 2021",
//         "program": "De Slimste Mens ter Wereld",
//         "img": "https://images.viervijfzes.be/www.vier.be/production/meta/schermafbeelding-2020-12-08-om-132536-ql0t7l.png?auto=format&fit=crop&h=452&ixlib=php-1.1.0&q=65&w=682&s=0d941f3fd855ee0a733b6ced587ff620"
//     },
//         "highlight": {
//         "intro": [
//         "Wie weet dat Tinder niet <em>de</em> beste plaats is voor het vinden van het lief van je dromen, maar ook dat je bij het rijden van een scheve schaats erg ongelukkig ten val kan komen?"
//         ],
//         "title": [
//         "Maak kans op <em>de</em> <em>Slimste</em> Scheurkalender ter Wereld 2021"
//         ],
//         "body": [
//         "<p>Wie weet dat Tinder niet <em>de</em> beste plaats is voor het vinden van het lief van je dromen, maar ook dat je bij het"
//         ]
//     }
//     },
//     {
//         "_index": "production",
//         "_type": "page",
//         "_id": "vier:node:en:35367",
//         "_score": 10.416418,
//         "_source": {
//         "id": "35367",
//         "type": "node",
//         "bundle": "video",
//         "url": "https://www.vier.be/video/de-slimste-mens-ter-wereld/de-slimste-scheurkalender-ter-wereld-2021-ligt-in-de-rekken",
//         "language": "en",
//         "title": "De Slimste Scheurkalender ter Wereld 2021 ligt in de rekken!",
//         "site": "vier",
//         "intro": "Wie weet dat Tinder niet de beste plaats is voor het vinden van het lief van je dromen, maar ook dat je bij het rijden van een scheve schaats erg ongelukkig ten val kan komen? ",
//         "created": 1607430868,
//         "changed": 1607431875,
//         "body": [],
//         "terms": [],
//         "suggest": "De Slimste Scheurkalender ter Wereld 2021 ligt in de rekken!",
//         "program": "De Slimste Mens ter Wereld",
//         "img": "https://images.viervijfzes.be/www.vier.be/production/meta/schermafbeelding-2020-12-08-om-132536-ql0tlg.png?auto=format&fit=crop&h=452&ixlib=php-1.1.0&q=65&w=682&s=52221e7b419e2bc82241761dc535bc17",
//         "duration": 0
//     },
//         "highlight": {
//         "intro": [
//         "Wie weet dat Tinder niet <em>de</em> beste plaats is voor het vinden van het lief van je dromen, maar ook dat je bij het rijden van een scheve schaats erg ongelukkig ten val kan komen? "
//         ],
//         "title": [
//         "<em>De</em> <em>Slimste</em> Scheurkalender ter Wereld 2021 ligt in <em>de</em> rekken!"
//         ]
//     }
//     },
//     {
//         "_index": "production",
//         "_type": "page",
//         "_id": "vier:node:en:35351",
//         "_score": 10.223076,
//         "_source": {
//         "id": "35351",
//         "type": "node",
//         "bundle": "video",
//         "url": "https://www.vier.be/video/de-slimste-mens-ter-wereld/de-slimste-mens-ter-wereld-s18/de-slimste-mens-ter-wereld-s18-aflevering-34",
//         "language": "en",
//         "title": "De Slimste Mens Ter Wereld - S18 - Aflevering 34",
//         "site": "vier",
//         "intro": "Erik Van Looy gaat in het 18e seizoen opnieuw op zoek naar De Slimste Mens ter Wereld in het 18e seizoen van de populaire quiz met Ella Leyers, Wesley Sonck, Pedro Elias, Liesbeth Van Impe, Ben Crabbé",
//         "created": 1607461140,
//         "changed": 1607511676,
//         "body": [],
//         "terms": [
//         "De Slimste Mens ter Wereld",
//         "Volledige Aflevering"
//         ],
//         "suggest": "De Slimste Mens Ter Wereld - S18 - Aflevering 34",
//         "program": "De Slimste Mens ter Wereld",
//         "img": "https://images.viervijfzes.be/www.vier.be/production/meta/34-ql0eez.png?auto=format&fit=crop&h=452&ixlib=php-1.1.0&q=65&w=682&s=d6d943f4988e3b15a7105bfbc816db2b",
//         "duration": 3358
//     },
//         "highlight": {
//         "intro": [
//         "Erik Van Looy gaat in het 18e seizoen opnieuw op zoek naar <em>De</em> <em>Slimste</em> Mens ter Wereld in het 18e seizoen van <em>de</em>"
//         ],
//         "title": [
//         "<em>De</em> <em>Slimste</em> Mens Ter Wereld - S18 - Aflevering 34"
//         ]
//     }
//     },
//     {
//         "_index": "production",
//         "_type": "page",
//         "_id": "vier:node:en:35321",
//         "_score": 9.529747,
//         "_source": {
//         "id": "35321",
//         "type": "node",
//         "bundle": "video",
//         "url": "https://www.vier.be/video/de-slimste-mens-ter-wereld/de-slimste-mens-ter-wereld-s18/de-slimste-mens-ter-wereld-s18-aflevering-33",
//         "language": "en",
//         "title": "De Slimste Mens Ter Wereld - S18 - Aflevering 33",
//         "site": "vier",
//         "intro": "Erik Van Looy gaat in het 18e seizoen opnieuw op zoek naar De Slimste Mens ter Wereld in het 18e seizoen van de populaire quiz met Ella Leyers, Wesley Sonck, Pedro Elias, Liesbeth Van Impe, Ben Crabbé",
//         "created": 1607373600,
//         "changed": 1607512004,
//         "body": [],
//         "terms": [
//         "De Slimste Mens ter Wereld",
//         "Volledige Aflevering"
//         ],
//         "suggest": "De Slimste Mens Ter Wereld - S18 - Aflevering 33",
//         "program": "De Slimste Mens ter Wereld",
//         "img": "https://images.viervijfzes.be/www.vier.be/production/meta/33-qkyjav.png?auto=format&fit=crop&h=452&ixlib=php-1.1.0&q=65&w=682&s=8b4f5848ab14bbd273113d513d4cc8ed",
//         "duration": 3253
//     },
//         "highlight": {
//         "intro": [
//         "Erik Van Looy gaat in het 18e seizoen opnieuw op zoek naar <em>De</em> <em>Slimste</em> Mens ter Wereld in het 18e seizoen van <em>de</em>"
//         ],
//         "title": [
//         "<em>De</em> <em>Slimste</em> Mens Ter Wereld - S18 - Aflevering 33"
//         ]
//     }
//     },
//     {
//         "_index": "production",
//         "_type": "page",
//         "_id": "vier:node:en:33263",
//         "_score": 7.713771,
//         "_source": {
//         "id": "33263",
//         "type": "node",
//         "bundle": "program",
//         "url": "https://www.vier.be/tweedehands-deluxe",
//         "language": "en",
//         "title": "Tweedehands Deluxe",
//         "site": "vier",
//         "intro": "Welkom in de wereld van het pandjeshuis. Een wereld waar geld baas is.",
//         "created": 1607814035,
//         "changed": 1594645182,
//         "body": [],
//         "terms": [
//         "Tweedehands Deluxe"
//         ],
//         "suggest": "Tweedehands Deluxe",
//         "program": "",
//         "img": "https://images.viervijfzes.be/www.vier.be/production/meta/unknown-2-qderz4.jpeg?auto=format&fit=crop&h=452&ixlib=php-1.1.0&q=65&w=682&s=4605aadfe36667dca8be3eadfdc91999"
//     },
//         "highlight": {
//         "intro": [
//         "Welkom in <em>de</em> wereld van het pandjeshuis. Een wereld waar geld baas is."
//         ],
//         "title": [
//         "Tweedehands <em>Deluxe</em>"
//         ]
//     }
//     },
//     {
//         "_index": "production",
//         "_type": "page",
//         "_id": "vier:node:en:24837",
//         "_score": 7.7137647,
//         "_source": {
//         "id": "24837",
//         "type": "node",
//         "bundle": "program",
//         "url": "https://www.vier.be/de-ambassade",
//         "language": "en",
//         "title": "De Ambassade",
//         "site": "vier",
//         "intro": "Voor Belgen in het buitenland is de ambassade een veilige haven. Hier kunnen ze terecht bij diefstal, bij conflictsituaties, een huwelijk met iemand uit en in het buitenland en zoveel meer.",
//         "created": 1607814034,
//         "changed": 1581333698,
//         "body": [
//         "<p>Voor Belgen in het buitenland is de ambassade een veilige haven. Hier kunnen ze terecht bij diefstal, bij conflictsituaties, een huwelijk met iemand uit en in het buitenland en zoveel meer.&nbsp;De uitzendingen van 'De Ambassade'&nbsp;tonen&nbsp;voornamelijk consulaire activiteiten, wat maar een deel van het werk van onze ambassades is. Achter de schermen is hun behandeling ook vaak complexer.</p>\r\n"
//         ],
//         "terms": [
//         "De Ambassade"
//         ],
//         "suggest": "De Ambassade",
//         "program": "",
//         "img": "https://images.viervijfzes.be/www.vier.be/production/meta/dlt5psda-q5hgvd.jpeg?auto=format&fit=crop&h=452&ixlib=php-1.1.0&q=65&w=682&s=4e28dfc63525ecaae0756d6ecd0d4388"
//     },
//         "highlight": {
//         "intro": [
//         "Voor Belgen in het buitenland is <em>de</em> ambassade een veilige haven. Hier kunnen ze terecht bij diefstal, bij"
//         ],
//         "title": [
//         "<em>De</em> Ambassade"
//         ],
//         "body": [
//         "<p>Voor Belgen in het buitenland is <em>de</em> ambassade een veilige haven. Hier kunnen ze terecht bij diefstal, bij"
//         ]
//     }
//     },
//     {
//         "_index": "production",
//         "_type": "page",
//         "_id": "vier:node:en:28699",
//         "_score": 7.7137647,
//         "_source": {
//         "id": "28699",
//         "type": "node",
//         "bundle": "program",
//         "url": "https://www.vier.be/de-battle",
//         "language": "en",
//         "title": "De Battle",
//         "site": "vier",
//         "intro": "Gert Verhulst en James Cooke pakken uit met een grootse studioshow: De Battle. Het duo bereidt zich al sinds januari voor op spectaculaire en aartsmoeilijke opdrachten. Binnenkort meer!",
//         "created": 1607814034,
//         "changed": 1581327948,
//         "body": [],
//         "terms": [
//         "James Cooke",
//         "Gert Verhulst",
//         "Katja Schuurman",
//         "De Battle",
//         "Najib Amhali"
//         ],
//         "suggest": "De Battle",
//         "program": "",
//         "img": "https://images.viervijfzes.be/www.vier.be/production/meta/tnwallpaper3840x2160debattle-q5hcfv.jpg?auto=format&fit=crop&h=452&ixlib=php-1.1.0&q=65&w=682&s=3f029d30d17bd373fdf8de1f387505a6"
//     },
//         "highlight": {
//         "intro": [
//         "Gert Verhulst en James Cooke pakken uit met een grootse studioshow: <em>De</em> Battle. Het duo bereidt zich al sinds januari"
//         ],
//         "title": [
//         "<em>De</em> Battle"
//         ]
//     }
//     },
//     {
//         "_index": "production",
//         "_type": "page",
//         "_id": "vier:node:en:29489",
//         "_score": 7.7137647,
//         "_source": {
//         "id": "29489",
//         "type": "node",
//         "bundle": "program",
//         "url": "https://www.vier.be/de-premier",
//         "language": "en",
//         "title": "De Premier",
//         "site": "vier",
//         "intro": "De Belgische eerste minister wordt ontvoerd. Hij zal weer worden vrijgelaten op voorwaarde dat hij de persoon, die hij later die dag tijdens een exclusief gesprek zal ontmoeten, vermoordt.",
//         "created": 1607814034,
//         "changed": 1598943301,
//         "body": [],
//         "terms": [],
//         "suggest": "De Premier",
//         "program": "",
//         "img": "https://images.viervijfzes.be/www.vier.be/production/meta/depremierstaand-q1xnbh.png?auto=format&fit=crop&h=452&ixlib=php-1.1.0&q=65&w=682&s=882414446851fbbbe14a9db70ca64e22"
//     },
//         "highlight": {
//         "intro": [
//         "<em>De</em> Belgische eerste minister wordt ontvoerd. Hij zal weer worden vrijgelaten op voorwaarde dat hij <em>de</em> persoon, die hij"
//         ],
//         "title": [
//         "<em>De</em> Premier"
//         ]
//     }
//     },
//     {
//         "_index": "production",
//         "_type": "page",
//         "_id": "vier:node:en:17627",
//         "_score": 7.7137585,
//         "_source": {
//         "id": "17627",
//         "type": "node",
//         "bundle": "program",
//         "url": "https://www.vier.be/de-dag",
//         "language": "en",
//         "title": "De Dag",
//         "site": "vier",
//         "intro": "'De Dag' is de eerste fictiereeks van Jonas Geirnaert en Julie Mahieu, geregisseerd door Gilles Coulier en Dries Vos.",
//         "created": 1607814033,
//         "changed": 1581333386,
//         "body": [
//         "<p>Een kleine stad wordt opgeschrikt door een uit de hand gelopen bankoverval. Een dag lang ontspint zich een uitermate spannend kat-en-muisspel tussen politie en&nbsp;gijzelnemers.&nbsp;De kijker leert al snel dat niet alles is wat het lijkt en ontdekt bij iedere aflevering een nieuw stuk van de puzzel.</p>\r\n\r\n<p>'De Dag' is de eerste fictiereeks van Jonas Geirnaert en Julie Mahieu, geregisseerd door Gilles Coulier&nbsp;en Dries Vos<strong>.&nbsp;</strong></p>\r\n\r\n<p><strong>De Dag, elke donderdag een dubbelaflevering, telkens om 20.35 uur en 21.25 uur.</strong></p>\r\n"
//         ],
//         "terms": [
//         "De Dag"
//         ],
//         "suggest": "De Dag",
//         "program": "",
//         "img": "https://images.viervijfzes.be/www.vier.be/production/meta/50w-3zua-q5hgds.jpeg?auto=format&fit=crop&h=452&ixlib=php-1.1.0&q=65&w=682&s=239b86bd85f895763394c534f96faf09"
//     },
//         "highlight": {
//         "intro": [
//         "'<em>De</em> Dag' is <em>de</em> eerste fictiereeks van Jonas Geirnaert en Julie Mahieu, geregisseerd door Gilles Coulier en Dries Vos."
//         ],
//         "title": [
//         "<em>De</em> Dag"
//         ],
//         "body": [
//         "<p>Een kleine stad wordt opgeschrikt door een uit <em>de</em> hand gelopen bankoverval. Een dag lang ontspint zich een uitermate"
//         ]
//     }
//     },
//     {
//         "_index": "production",
//         "_type": "page",
//         "_id": "vier:node:en:425",
//         "_score": 7.7137523,
//         "_source": {
//         "id": "425",
//         "type": "node",
//         "bundle": "program",
//         "url": "https://www.vier.be/de-sollicitatie",
//         "language": "en",
//         "title": "De Sollicitatie",
//         "site": "vier",
//         "intro": "Discreet opgestelde camera’s registreren echte sollicitatiegesprekken bij werkgevers op zoek naar de ideale werknemer. We leven mee met de kandidaten vóór, tijdens en na hun sollicitatie.",
//         "created": 1607814032,
//         "changed": 1581340472,
//         "body": [
//         "<p>Discreet opgestelde camera’s registreren echte sollicitatiegesprekken bij werkgevers op zoek naar de ideale werknemer. We leven mee met de kandidaten vóór, tijdens en na hun sollicitatie: van de nervositeit en de uitschuivers tot de gloriemomenten. Op het einde komen we te weten wie de nieuwe aanwinst wordt voor het bedrijf. De Sollicitatie is een herkenbaar programma met een competitief kantje, over het gesprek dat een leven kan veranderen.</p>\r\n"
//         ],
//         "terms": [
//         "De Sollicitatie",
//         "Solliciteren",
//         "Woestijnvis",
//         "werkgever",
//         "werknemer"
//         ],
//         "suggest": "De Sollicitatie",
//         "program": "",
//         "img": "https://images.viervijfzes.be/www.vier.be/production/meta/tnwallpaper3840x2160desollicitatie-q5hm3h.jpg?auto=format&fit=crop&h=452&ixlib=php-1.1.0&q=65&w=682&s=27c2e13a488e61e2eaec4d1a820030e0"
//     },
//         "highlight": {
//         "intro": [
//         "Discreet opgestelde camera’s registreren echte sollicitatiegesprekken bij werkgevers op zoek naar <em>de</em> ideale werknemer"
//         ],
//         "title": [
//         "<em>De</em> Sollicitatie"
//         ],
//         "body": [
//         "<p>Discreet opgestelde camera’s registreren echte sollicitatiegesprekken bij werkgevers op zoek naar <em>de</em> ideale"
//         ]
//     }
//     },
//     {
//         "_index": "production",
//         "_type": "page",
//         "_id": "vier:node:en:15129",
//         "_score": 7.7137523,
//         "_source": {
//         "id": "15129",
//         "type": "node",
//         "bundle": "program",
//         "url": "https://www.vier.be/de-boxys",
//         "language": "en",
//         "title": "De Boxy's",
//         "site": "vier",
//         "intro": "In het reality-programma De Boxy’s trekt de bekendste culinaire tweeling van Vlaanderen, Kristof en Stefan Boxy, op een eigenzinnig gastronomisch avontuur.",
//         "created": 1607814032,
//         "changed": 1581328008,
//         "body": [
//         "<p>In het reality-programma De Boxy’s trekt de bekendste culinaire tweeling van Vlaanderen, Kristof en Stefan Boxy, op een eigenzinnig gastronomisch avontuur. De Boxy’s zoeken van nature graag het avontuur op. Zo weigerden zij ooit een tweede Michelinster om een exclusieve traiteurzaak te beginnen. In De Boxy’s willen ze hun grenzen blijven verleggen.</p>\r\n\r\n<p>Hiervoor laten ze zich elke week uitdagen door een bekende of onbekende Vlaming met een bijzondere kookopdracht. Die zorgt er voor dat zij hun wereldbeeld verruimen en zelfs als doorwinterde foodies verrast blijven worden van wat de wereld te bieden heeft. De Boxy’s herinneren er ons aan dat je nooit te oud bent om te leren. Niet alleen op culinair vlak worden zij verrast, ook als mensen gaan ze elke week opnieuw 'out of the boxy'.</p>\r\n"
//         ],
//         "terms": [
//         "De Boxy's",
//         "Kristof Boxy",
//         "Stefan Boxy",
//         "Koken",
//         "Culinair",
//         "topchefs",
//         "Traiteurs"
//         ],
//         "suggest": "De Boxy's",
//         "program": "",
//         "img": "https://images.viervijfzes.be/www.vier.be/production/meta/deboxysphd3840x2160-q5hchf.jpg?auto=format&fit=crop&h=452&ixlib=php-1.1.0&q=65&w=682&s=7465cec866616c58f30b0c69c0cef972"
//     },
//         "highlight": {
//         "intro": [
//         "In het reality-programma <em>De</em> Boxy’s trekt <em>de</em> bekendste culinaire tweeling van Vlaanderen, Kristof en Stefan Boxy, op een eigenzinnig gastronomisch avontuur."
//         ],
//         "title": [
//         "<em>De</em> Boxy's"
//         ],
//         "body": [
//         "<p>In het reality-programma <em>De</em> Boxy’s trekt <em>de</em> bekendste culinaire tweeling van Vlaanderen, Kristof en Stefan Boxy, op"
//         ]
//     }
//     },
//     {
//         "_index": "production",
//         "_type": "page",
//         "_id": "vier:node:en:313",
//         "_score": 7.713746,
//         "_source": {
//         "id": "313",
//         "type": "node",
//         "bundle": "program",
//         "url": "https://www.vier.be/de-recherche",
//         "language": "en",
//         "title": "De Recherche",
//         "site": "vier",
//         "intro": "De Recherche is terug. De camera volgt dit seizoen elke stap in het onderzoek van de Lokale Recherche in Genk en Gent.",
//         "created": 1607814031,
//         "changed": 1583004902,
//         "body": [
//         "<p>De Recherche is terug. De camera volgt dit seizoen elke stap in het onderzoek van de Lokale Recherche in Genk en Gent. Politiezone Carma, onder leiding van Rudi Schellingen, toont de dagelijkse werking van team drugs, team Ecofin (financiële dossiers) en team zware criminaliteit. Voor de allereerste keer kon De Recherche ook enkele zedendelicten volgen. Het opsporen en identificeren van mogelijke daders, het verzamelen van het nodige bewijsmateriaal, huiszoekingen en observaties van de buurt of verdachten, tot de arrestatie en het eerste verhoor door de rechercheurs en de voorleiding bij de onderzoeksrechter.</p>\r\n"
//         ],
//         "terms": [
//         "De Recherche",
//         "Onderzoek"
//         ],
//         "suggest": "De Recherche",
//         "program": "",
//         "img": "https://images.viervijfzes.be/www.vier.be/production/meta/kqaxksvq-q5hg6r.jpeg?auto=format&fit=crop&h=452&ixlib=php-1.1.0&q=65&w=682&s=97c2a1d3f8711c4c4d5188bd8cb99efe"
//     },
//         "highlight": {
//         "intro": [
//         "<em>De</em> Recherche is terug. <em>De</em> camera volgt dit seizoen elke stap in het onderzoek van <em>de</em> Lokale Recherche in Genk en Gent."
//         ],
//         "title": [
//         "<em>De</em> Recherche"
//         ],
//         "body": [
//         "<p><em>De</em> Recherche is terug. <em>De</em> camera volgt dit seizoen elke stap in het onderzoek van <em>de</em> Lokale Recherche in Genk en"
//         ]
//     }
//     },
//     {
//         "_index": "production",
//         "_type": "page",
//         "_id": "vier:node:en:337",
//         "_score": 7.713746,
//         "_source": {
//         "id": "337",
//         "type": "node",
//         "bundle": "program",
//         "url": "https://www.vier.be/de-mol",
//         "language": "en",
//         "title": "De Mol",
//         "site": "vier",
//         "intro": "Tien onbekende Vlamingen, Gilles De Coster en met hen ook een klein miljoen mollenvangers, maken zich op voor De Mol Griekenland!",
//         "created": 1607814031,
//         "changed": 1602756001,
//         "body": [],
//         "terms": [],
//         "suggest": "De Mol",
//         "program": "",
//         "img": "https://images.viervijfzes.be/www.vier.be/production/meta/tnwallpaper3840x2160demol2020-q5y2dw.jpg?auto=format&fit=crop&h=452&ixlib=php-1.1.0&q=65&w=682&s=d70bbf2dbd6558a6fd8e9610b884043a"
//     },
//         "highlight": {
//         "intro": [
//         "Tien onbekende Vlamingen, Gilles <em>De</em> Coster en met hen ook een klein miljoen mollenvangers, maken zich op voor <em>De</em> Mol Griekenland!"
//         ],
//         "title": [
//         "<em>De</em> Mol"
//         ]
//     }
//     },
//     {
//         "_index": "production",
//         "_type": "page",
//         "_id": "vier:node:en:351",
//         "_score": 7.713746,
//         "_source": {
//         "id": "351",
//         "type": "node",
//         "bundle": "program",
//         "url": "https://www.vier.be/de-idioten",
//         "language": "en",
//         "title": "De Idioten",
//         "site": "vier",
//         "intro": "De Idioten zijn terug en doen het ditmaal solo. Pedro Elias en Sarah Vandeursen zoeken proefondervindelijk een antwoord op de vragen die iemand anders zich stelt. ",
//         "created": 1607814031,
//         "changed": 1590832801,
//         "body": [
//         "<p>De Idioten zijn terug en doen het ditmaal solo. Pedro Elias en Sarah Vandeursen zoeken proefondervindelijk een antwoord op de vragen die iemand anders zich stelt. Dit seizoen nodigen ze elke week een andere gast uit die hen onderwerpt aan zijn of haar vragen. Vaak omdat de zoektocht té moeilijk of te pijnlijk is...</p>\r\n"
//         ],
//         "terms": [],
//         "suggest": "De Idioten",
//         "program": "",
//         "img": "https://images.viervijfzes.be/www.vier.be/production/meta/deidiotenphd3840x2160px-q5hfzi.jpg?auto=format&fit=crop&h=452&ixlib=php-1.1.0&q=65&w=682&s=7c1379b4bf82db5739ff302ca8eead80"
//     },
//         "highlight": {
//         "intro": [
//         "<em>De</em> Idioten zijn terug en doen het ditmaal solo. Pedro Elias en Sarah Vandeursen zoeken proefondervindelijk een antwoord op <em>de</em> vragen die iemand anders zich stelt. "
//         ],
//         "title": [
//         "<em>De</em> Idioten"
//         ],
//         "body": [
//         "<p><em>De</em> Idioten zijn terug en doen het ditmaal solo. Pedro Elias en Sarah Vandeursen zoeken proefondervindelijk een"
//         ]
//     }
//     },
//     {
//         "_index": "production",
//         "_type": "page",
//         "_id": "vier:node:en:227",
//         "_score": 7.713746,
//         "_source": {
//         "id": "227",
//         "type": "node",
//         "bundle": "program",
//         "url": "https://www.vier.be/de-rechtbank",
//         "language": "en",
//         "title": "De Rechtbank",
//         "site": "vier",
//         "intro": "De procureurs eisen, de beklaagden verdedigen zich, de advocaten pleiten en de rechters oordelen. En dit in eer en geweten.",
//         "created": 1607814031,
//         "changed": 1602581654,
//         "body": [],
//         "terms": [
//         "De Rechtbank",
//         "Advocaten",
//         "Rechters",
//         "Beklaagden",
//         "Rechtzaak",
//         "Straf"
//         ],
//         "suggest": "De Rechtbank",
//         "program": "",
//         "img": "https://images.viervijfzes.be/www.vier.be/production/meta/vod-online3840x2160de-rechtbank-qfknoy.jpg?auto=format&fit=crop&h=452&ixlib=php-1.1.0&q=65&w=682&s=32a742a40565bdb777b24056c854340a"
//     },
//         "highlight": {
//         "intro": [
//         "<em>De</em> procureurs eisen, <em>de</em> beklaagden verdedigen zich, <em>de</em> advocaten pleiten en <em>de</em> rechters oordelen. En dit in eer en geweten."
//         ],
//         "title": [
//         "<em>De</em> Rechtbank"
//         ]
//     }
//     }
//     ]
// }
// }

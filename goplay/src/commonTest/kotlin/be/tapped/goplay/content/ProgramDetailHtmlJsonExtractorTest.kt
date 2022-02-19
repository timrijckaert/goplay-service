package be.tapped.goplay.content

import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.ShouldSpec

internal class ProgramDetailHtmlJsonExtractorTest : ShouldSpec({
    should("extract the program JSON information from the HTML page") {
        val programDetailJson = ProgramDetailHtmlJsonExtractor().parse(DE_SLIMSTE_MENS_TER_WERELD)
        programDetailJson.shouldBeRight()
    }
})

private const val DE_SLIMSTE_MENS_TER_WERELD =
    """<!DOCTYPE html>
<html>
<head>
    <title>De Slimste Mens ter Wereld</title>
</head>
<body>
<div data-hero="{
  &quot;data&quot;: {
    &quot;id&quot;: &quot;ddd329e7-e902-4c6e-8754-bf170c2e9013&quot;,
    &quot;title&quot;: &quot;De Slimste Mens ter Wereld&quot;,
    &quot;subtitle&quot;: &quot;&quot;,
    &quot;description&quot;: &quot;Tien weken en veertig bijeenkomsten lang houden wij een openbaar onderzoek onder leiding van de immer nieuwsgierige vorser Erik Van Looy.&quot;,
    &quot;brand&quot;: &quot;vier&quot;,
    &quot;type&quot;: &quot;default&quot;,
    &quot;category&quot;: &quot;Entertainment&quot;,
    &quot;label&quot;: &quot;&quot;,
    &quot;link&quot;: &quot;\/de-slimste-mens-ter-wereld&quot;,
    &quot;images&quot;: {
      &quot;hero&quot;: &quot;https:\/\/wmimages.goplay.be\/styles\/f7331df9e0140370408293ff5186b3cc1ba2ff25\/meta\/goplaywp3840x2160dsmtws18-r07pbn.jpg?style=W3sianBlZyI6eyJxdWFsaXR5Ijo5MH19LHsicmVzaXplIjp7ImZpdCI6ImNvdmVyIiwid2lkdGgiOjM4NDAsImhlaWdodCI6MjE2MCwiZ3Jhdml0eSI6bnVsbCwid2l0aG91dEVubGFyZ2VtZW50IjpmYWxzZX19XQ==&amp;sign=30fb11d581ce2d0c5597f35ce20e45a4d3ef17043d6e6788b99349faa5498e4a&quot;,
      &quot;mobile&quot;: &quot;https:\/\/wmimages.goplay.be\/styles\/2188197a2952bed30ab69f31779dff356336183d\/2021-09\/goplaympzpl1050x1500dsmtws18-r07pcg.jpg?style=W3sianBlZyI6eyJxdWFsaXR5Ijo5MH19LHsicmVzaXplIjp7ImZpdCI6ImNvdmVyIiwid2lkdGgiOjc1MCwiaGVpZ2h0IjoxMDAwLCJncmF2aXR5IjpudWxsLCJ3aXRob3V0RW5sYXJnZW1lbnQiOmZhbHNlfX1d&amp;sign=1d8753a7d86240a7cf37f344979ab1dc9c9f31c8a5b9af3a1dfa9fa4673cf897&quot;,
      &quot;poster&quot;: &quot;https:\/\/wmimages.goplay.be\/styles\/d03a19a83908ec0fa0ca867113b0553c8bce6d67\/2021-09\/goplaympmpl1050x1500dsmtws18-r07pc9.jpg?style=W3sicmVzaXplIjp7ImZpdCI6Imluc2lkZSIsIndpZHRoIjo0MDAsImhlaWdodCI6NTgwLCJ3aXRob3V0RW5sYXJnZW1lbnQiOmZhbHNlfX0seyJqcGVnIjp7InF1YWxpdHkiOjk1fX1d&amp;sign=eca9a370304c329a0b2aab01b199158169fb25a74f601b2a9521fd3f861a7ec9&quot;,
      &quot;teaser&quot;: &quot;https:\/\/wmimages.goplay.be\/styles\/2a4e9f9d7142af06c50511f683da39ad08740d60\/meta\/goplaywp3840x2160dsmtws18-r07pbn.jpg?style=W3sicmVzaXplIjp7ImZpdCI6ImNvdmVyIiwid2lkdGgiOjY3MCwiaGVpZ2h0IjozNzAsImdyYXZpdHkiOiJjZW50ZXIiLCJ3aXRob3V0RW5sYXJnZW1lbnQiOmZhbHNlfX0seyJqcGVnIjp7InF1YWxpdHkiOjg1fX1d&amp;sign=6a8afe0d10db69053f81a1d40d3c17537d350660b67c323858a9e033e2f9d8a0&quot;,
      &quot;moviePoster&quot;: &quot;https:\/\/wmimages.goplay.be\/styles\/fc9f8afb7a8416cc9fcf5a03e3f4fb0fee0b3ef0\/2021-09\/goplaympmpl1050x1500dsmtws18-r07pc9.jpg?style=W3sicmVzaXplIjp7ImZpdCI6Imluc2lkZSIsIndpZHRoIjoxMDIwLCJoZWlnaHQiOjE0ODAsIndpdGhvdXRFbmxhcmdlbWVudCI6ZmFsc2V9fSx7ImpwZWciOnsicXVhbGl0eSI6OTV9fV0=&amp;sign=6ac6be40d6fc9d1183ac099509d95e9e11f0d1b3db5708eae0ed1af40f500951&quot;
    },
    &quot;header&quot;: {
      &quot;title&quot;: &quot;&quot;,
      &quot;video&quot;: []
    },
    &quot;needs16PlusLabel&quot;: false,
    &quot;pageInfo&quot;: {
      &quot;author&quot;: &quot;Wouter&quot;,
      &quot;brand&quot;: &quot;Play4&quot;,
      &quot;description&quot;: &quot;Tien weken en veertig bijeenkomsten lang houden wij een openbaar onderzoek onder leiding van de immer nieuwsgierige vorser Erik Van Looy.&quot;,
      &quot;nodeId&quot;: &quot;2923&quot;,
      &quot;nodeUuid&quot;: &quot;ddd329e7-e902-4c6e-8754-bf170c2e9013&quot;,
      &quot;notificationsScore&quot;: 0,
      &quot;program&quot;: &quot;De Slimste Mens ter Wereld&quot;,
      &quot;programKey&quot;: &quot;de_slimste_mens_ter_wereld&quot;,
      &quot;programId&quot;: &quot;2923&quot;,
      &quot;programUuid&quot;: &quot;ddd329e7-e902-4c6e-8754-bf170c2e9013&quot;,
      &quot;publishDate&quot;: 1346765983,
      &quot;title&quot;: &quot;De Slimste Mens ter Wereld&quot;,
      &quot;type&quot;: &quot;program&quot;,
      &quot;tags&quot;: [
        &quot;De Slimste Mens ter Wereld&quot;,
        &quot;Quiz&quot;,
        &quot;erik van looy&quot;,
        &quot;Jeroom&quot;,
        &quot;Maaike Cafmeyer&quot;,
        &quot;Bart Cannaerts&quot;,
        &quot;Gilles Van Bouwel&quot;,
        &quot;Philippe Geubels&quot;,
        &quot;Marc-Marie Huijbregts&quot;,
        &quot;Sam Gooris&quot;,
        &quot;jonas geirnaert&quot;,
        &quot;Jan-Jaap Van der Wal&quot;,
        &quot;Herman Brusselmans&quot;,
        &quot;Karen Damen&quot;,
        &quot;Stefaan Degand&quot;,
        &quot;Wim Helsen&quot;
      ],
      &quot;unpublishDate&quot;: 0,
      &quot;url&quot;: &quot;https:\/\/www.goplay.be\/de-slimste-mens-ter-wereld&quot;
    },
    &quot;playlists&quot;: [
      {
        &quot;episodes&quot;: [
          {
            &quot;autoplay&quot;: false,
            &quot;badge&quot;: &quot;&quot;,
            &quot;cimTag&quot;: &quot;vid.tvi.ep.vod.free&quot;,
            &quot;createdDate&quot;: 1571082600,
            &quot;description&quot;: &quot;&lt;p&gt;Erik Van Looy gaat opnieuw op zoek naar De Slimste Mens ter Wereld. Het jurygestoelte wordt opgeblonken voor maar liefst 25 geweldige bijzitters.&lt;\/p&gt;\r\n&quot;,
            &quot;duration&quot;: 3492,
            &quot;embedCta&quot;: null,
            &quot;enablePreroll&quot;: true,
            &quot;episodeNumber&quot;: 1,
            &quot;episodeTitle&quot;: &quot;S17 - Aflevering 1&quot;,
            &quot;hasProductPlacement&quot;: true,
            &quot;image&quot;: &quot;https:\/\/wmimages.goplay.be\/styles\/f292bf93422e4ebb269e7cae3e54c856c32274c5\/meta\/deslimstemensy17e1-15-pzdcm3_0.jpg?style=W3sianBlZyI6eyJxdWFsaXR5Ijo2NX19LHsicmVzaXplIjp7ImZpdCI6Imluc2lkZSIsIndpZHRoIjoxMjgwLCJoZWlnaHQiOjcyMCwid2l0aG91dEVubGFyZ2VtZW50Ijp0cnVlfX1d&amp;sign=325bc706fecf7e7bae89a15ce8ce9f417229817ffe9546bf14e8c3372cf734fd&quot;,
            &quot;isProtected&quot;: true,
            &quot;isSeekable&quot;: false,
            &quot;isStreaming&quot;: false,
            &quot;link&quot;: &quot;\/video\/de-slimste-mens-ter-wereld\/de-slimste-mens-ter-wereld-s17\/de-slimste-mens-ter-wereld-s17-aflevering-1&quot;,
            &quot;midrollOffsets&quot;: [
              1379,
              2746
            ],
            &quot;needs16PlusLabel&quot;: false,
            &quot;pageInfo&quot;: {
              &quot;author&quot;: &quot;jelle.dhondt@sbsbelgium.be&quot;,
              &quot;brand&quot;: &quot;Play4&quot;,
              &quot;description&quot;: &quot;Erik Van Looy gaat opnieuw op zoek naar De Slimste Mens ter Wereld. Het jurygestoelte wordt opgeblonken voor maar liefst 25 geweldige bijzitters.&quot;,
              &quot;nodeId&quot;: &quot;4266&quot;,
              &quot;nodeUuid&quot;: &quot;488c9c0d-2e0c-499c-93c5-286994a0f313&quot;,
              &quot;notificationsScore&quot;: 0,
              &quot;program&quot;: &quot;De Slimste Mens ter Wereld&quot;,
              &quot;programKey&quot;: &quot;de_slimste_mens_ter_wereld&quot;,
              &quot;programId&quot;: &quot;2923&quot;,
              &quot;programUuid&quot;: &quot;ddd329e7-e902-4c6e-8754-bf170c2e9013&quot;,
              &quot;publishDate&quot;: 1571082600,
              &quot;title&quot;: &quot;De Slimste Mens Ter Wereld - S17 - Aflevering 1&quot;,
              &quot;type&quot;: &quot;video-long_form&quot;,
              &quot;tags&quot;: [
                &quot;De Slimste Mens ter Wereld&quot;,
                &quot;Volledige Aflevering&quot;
              ],
              &quot;unpublishDate&quot;: 1728849000,
              &quot;url&quot;: &quot;https:\/\/www.goplay.be\/video\/de-slimste-mens-ter-wereld\/de-slimste-mens-ter-wereld-s17\/de-slimste-mens-ter-wereld-s17-aflevering-1&quot;
            },
            &quot;pageUuid&quot;: &quot;488c9c0d-2e0c-499c-93c5-286994a0f313&quot;,
            &quot;parentalRating&quot;: &quot;AL&quot;,
            &quot;path&quot;: &quot;&quot;,
            &quot;program&quot;: {
              &quot;title&quot;: &quot;De Slimste Mens ter Wereld&quot;,
              &quot;poster&quot;: &quot;https:\/\/wmimages.goplay.be\/styles\/d03a19a83908ec0fa0ca867113b0553c8bce6d67\/2021-09\/goplaympmpl1050x1500dsmtws18-r07pc9.jpg?style=W3sicmVzaXplIjp7ImZpdCI6Imluc2lkZSIsIndpZHRoIjo0MDAsImhlaWdodCI6NTgwLCJ3aXRob3V0RW5sYXJnZW1lbnQiOmZhbHNlfX0seyJqcGVnIjp7InF1YWxpdHkiOjk1fX1d&amp;sign=eca9a370304c329a0b2aab01b199158169fb25a74f601b2a9521fd3f861a7ec9&quot;,
              &quot;category&quot;: &quot;Entertainment&quot;
            },
            &quot;seasonNumber&quot;: 17,
            &quot;seekableFrom&quot;: 1571086092,
            &quot;title&quot;: &quot;De Slimste Mens Ter Wereld - S17 - Aflevering 1&quot;,
            &quot;tracking&quot;: {
              &quot;item_name&quot;: &quot;De Slimste Mens ter Wereld&quot;,
              &quot;item_id&quot;: &quot;ddd329e7-e902-4c6e-8754-bf170c2e9013&quot;,
              &quot;item_brand&quot;: &quot;Play4&quot;,
              &quot;item_category&quot;: &quot;Entertainment&quot;,
              &quot;item_variant&quot;: &quot;S17 - Aflevering 1&quot;
            },
            &quot;type&quot;: &quot;long_form&quot;,
            &quot;unpublishDate&quot;: 1728849000,
            &quot;videoUuid&quot;: &quot;797fe037-291c-408f-b0c9-bd7f7e4bdedb&quot;,
            &quot;whatsonId&quot;: &quot;10138491045411527&quot;
          },
        ],
        &quot;id&quot;: &quot;c2996c40-eb62-41db-80c2-a4ca3c5c9ef9&quot;,
        &quot;link&quot;: &quot;\/video\/de-slimste-mens-ter-wereld\/de-slimste-mens-ter-wereld-s17&quot;,
        &quot;pageInfo&quot;: {
          &quot;author&quot;: &quot;Wouter&quot;,
          &quot;brand&quot;: &quot;Play4&quot;,
          &quot;description&quot;: &quot;Erik Van Looy gaat opnieuw op zoek naar De Slimste Mens ter Wereld. Het jurygestoelte wordt opgeblonken voor maar liefst 25 geweldige bijzitters.&quot;,
          &quot;nodeId&quot;: &quot;6344&quot;,
          &quot;nodeUuid&quot;: &quot;c2996c40-eb62-41db-80c2-a4ca3c5c9ef9&quot;,
          &quot;notificationsScore&quot;: 0,
          &quot;program&quot;: &quot;De Slimste Mens ter Wereld&quot;,
          &quot;programKey&quot;: &quot;de_slimste_mens_ter_wereld&quot;,
          &quot;programId&quot;: &quot;2923&quot;,
          &quot;programUuid&quot;: &quot;ddd329e7-e902-4c6e-8754-bf170c2e9013&quot;,
          &quot;publishDate&quot;: 1570806241,
          &quot;title&quot;: &quot;De Slimste Mens ter Wereld - Seizoen 17&quot;,
          &quot;type&quot;: &quot;playlist&quot;,
          &quot;tags&quot;: [],
          &quot;unpublishDate&quot;: 0,
          &quot;url&quot;: &quot;https:\/\/www.goplay.be\/video\/de-slimste-mens-ter-wereld\/de-slimste-mens-ter-wereld-s17&quot;
        },
        &quot;title&quot;: &quot;Seizoen 17&quot;
      },
    ],
    &quot;social&quot;: {
      &quot;facebook&quot;: &quot;http:\/\/www.facebook.com\/deslimstemensterwereld&quot;,
      &quot;hashtag&quot;: &quot;DSMTW&quot;,
      &quot;instagram&quot;: &quot;https:\/\/www.instagram.com\/deslimstemensterwereld\/&quot;,
      &quot;twitter&quot;: &quot;https:\/\/twitter.com\/dsmtw&quot;
    },
    &quot;tracking&quot;: {
      &quot;item_name&quot;: &quot;De Slimste Mens ter Wereld&quot;,
      &quot;item_id&quot;: &quot;ddd329e7-e902-4c6e-8754-bf170c2e9013&quot;,
      &quot;item_brand&quot;: &quot;Play4&quot;,
      &quot;item_category&quot;: &quot;Entertainment&quot;
    },
    &quot;movie&quot;: null,
    &quot;streamz&quot;: []
  }
}
">
</div>
</body>
</html>
    """

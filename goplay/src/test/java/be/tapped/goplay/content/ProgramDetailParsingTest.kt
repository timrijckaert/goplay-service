package be.tapped.goplay.content

import be.tapped.goplay.jsonSerializer
import be.tapped.goplay.safeDecodeFromString
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.ShouldSpec

internal class ProgramDetailParsingTest : ShouldSpec({
    should("be able to parse a program detail") {
        val programDetail = jsonSerializer.safeDecodeFromString<Program.Detail>(CONTENT_JSON)
        programDetail.shouldBeRight()
    }
})

private const val CONTENT_JSON: String =
    """{
  "id": "ae1e96df-0e96-44e6-95d1-51e162898e43",
  "title": "Culinaire Speurneuzen",
  "subtitle": "",
  "description": "In de tweede reeks van Culinaire Speurneuzen worden per aflevering een aantal gepassioneerde mensen geportretteerd die elk op hun eigen manier op zoek gaan naar de beste producten uit hun vakgebied.",
  "brand": "zeven",
  "type": "default",
  "category": "Lifestyle",
  "label": "",
  "link": "/culinaire-speurneuzen",
  "images": {
    "hero": "https://wmimages.goplay.be/styles/f7331df9e0140370408293ff5186b3cc1ba2ff25/meta/goplaywp3840x2160culinairespeurneuzen-r5e2vd.jpg?style=W3sianBlZyI6eyJxdWFsaXR5Ijo5MH19LHsicmVzaXplIjp7ImZpdCI6ImNvdmVyIiwid2lkdGgiOjM4NDAsImhlaWdodCI6MjE2MCwiZ3Jhdml0eSI6bnVsbCwid2l0aG91dEVubGFyZ2VtZW50IjpmYWxzZX19XQ==&sign=1e8b7437085e86154de936fc08f6d7308be99c092c291d7b2348f191aada475c",
    "mobile": "https://wmimages.goplay.be/styles/2188197a2952bed30ab69f31779dff356336183d/2022-01/goplaympmpl1050x1500culinairespeurneuzen-r5e2vv.jpg?style=W3sianBlZyI6eyJxdWFsaXR5Ijo5MH19LHsicmVzaXplIjp7ImZpdCI6ImNvdmVyIiwid2lkdGgiOjc1MCwiaGVpZ2h0IjoxMDAwLCJncmF2aXR5IjpudWxsLCJ3aXRob3V0RW5sYXJnZW1lbnQiOmZhbHNlfX1d&sign=4e01fcf6d9cba52f825791ade191376c826500d6f41e7659595028da6f46ec91",
    "poster": "https://wmimages.goplay.be/styles/d03a19a83908ec0fa0ca867113b0553c8bce6d67/2022-01/goplaympmpl1050x1500culinairespeurneuzen-r5e2vr.jpg?style=W3sicmVzaXplIjp7ImZpdCI6Imluc2lkZSIsIndpZHRoIjo0MDAsImhlaWdodCI6NTgwLCJ3aXRob3V0RW5sYXJnZW1lbnQiOmZhbHNlfX0seyJqcGVnIjp7InF1YWxpdHkiOjk1fX1d&sign=0b1531a5cdf973303368b2665fa1345b6c0ac4a07eadc4072a1ea9f28b98fbee",
    "teaser": "https://wmimages.goplay.be/styles/2a4e9f9d7142af06c50511f683da39ad08740d60/meta/goplaywp3840x2160culinairespeurneuzen-r5e2vd.jpg?style=W3sicmVzaXplIjp7ImZpdCI6ImNvdmVyIiwid2lkdGgiOjY3MCwiaGVpZ2h0IjozNzAsImdyYXZpdHkiOiJjZW50ZXIiLCJ3aXRob3V0RW5sYXJnZW1lbnQiOmZhbHNlfX0seyJqcGVnIjp7InF1YWxpdHkiOjg1fX1d&sign=9d2efd032c4ffcb80b331d945bd6d99153d74ce308c3d8fc101cfe9f31187b49",
    "moviePoster": "https://wmimages.goplay.be/styles/fc9f8afb7a8416cc9fcf5a03e3f4fb0fee0b3ef0/2022-01/goplaympmpl1050x1500culinairespeurneuzen-r5e2vr.jpg?style=W3sicmVzaXplIjp7ImZpdCI6Imluc2lkZSIsIndpZHRoIjoxMDIwLCJoZWlnaHQiOjE0ODAsIndpdGhvdXRFbmxhcmdlbWVudCI6ZmFsc2V9fSx7ImpwZWciOnsicXVhbGl0eSI6OTV9fV0=&sign=dd233b54d78d078accfe11a92f52fea4e3d7a1264b5a1f3284401a3b257a3d79"
  },
  "header": {
    "title": "",
    "video": []
  },
  "needs16PlusLabel": false,
  "pageInfo": {
    "author": "Axel.Florizoone@sbsbelgium.be",
    "brand": "Play7",
    "description": "In de tweede reeks van Culinaire Speurneuzen worden per aflevering een aantal gepassioneerde mensen geportretteerd die elk op hun eigen manier op zoek gaan naar de beste producten uit hun vakgebied.",
    "nodeId": "23069",
    "nodeUuid": "ae1e96df-0e96-44e6-95d1-51e162898e43",
    "notificationsScore": 0,
    "program": "Culinaire Speurneuzen",
    "programKey": "culinaire_speurneuzen",
    "programId": "23069",
    "programUuid": "ae1e96df-0e96-44e6-95d1-51e162898e43",
    "publishDate": 1641677580,
    "title": "Culinaire Speurneuzen",
    "type": "program",
    "tags": [],
    "unpublishDate": 0,
    "url": "https://www.goplay.be/culinaire-speurneuzen"
  },
  "playlists": [
    {
      "episodes": [
        {
          "autoplay": false,
          "badge": "",
          "cimTag": "vid.tvi.ep.vod.free",
          "createdDate": 1641677580,
          "description": "<p>In de tweede reeks van Culinaire Speurneuzen worden per aflevering een aantal gepassioneerde mensen geportretteerd die elk op hun eigen manier op zoek gaan naar de beste producten uit hun vakgebied.</p>\r\n",
          "duration": 2192,
          "embedCta": null,
          "enablePreroll": true,
          "episodeNumber": 1,
          "episodeTitle": "S2 - Aflevering 1",
          "hasProductPlacement": false,
          "image": "https://wmimages.goplay.be/styles/1bada4e8211a435bbbf8f296e4d394b16b4d7b15/meta/vlcsnap-2022-01-08-12h43m14s532-r5e38z.png?style=W3sicmVzaXplIjp7ImZpdCI6Imluc2lkZSIsIndpZHRoIjoxMjgwLCJoZWlnaHQiOjcyMCwid2l0aG91dEVubGFyZ2VtZW50Ijp0cnVlfX1d&sign=018a0a1f04e0d06fa5e0a0fcb032e945927f1dc5dc73b73c5a7ba0879fbda43a",
          "isProtected": true,
          "isSeekable": false,
          "isStreaming": false,
          "link": "/video/culinaire-speurneuzen/culinaire-speurneuzen/culinaire-speurneuzen-s2-aflevering-1",
          "midrollOffsets": [
            778,
            1551
          ],
          "needs16PlusLabel": false,
          "pageInfo": {
            "author": "Axel.Florizoone@sbsbelgium.be",
            "brand": "Play7",
            "description": "In de tweede reeks van Culinaire Speurneuzen worden per aflevering een aantal gepassioneerde mensen geportretteerd die elk op hun eigen manier op zoek gaan naar de beste producten uit hun vakgebied.",
            "nodeId": "23040",
            "nodeUuid": "cf5cdfa7-cad8-425b-a69f-dad1230c3a46",
            "notificationsScore": 0,
            "program": "Culinaire Speurneuzen",
            "programKey": "culinaire_speurneuzen",
            "programId": "23069",
            "programUuid": "ae1e96df-0e96-44e6-95d1-51e162898e43",
            "publishDate": 1641677580,
            "title": "Culinaire Speurneuzen - S2 - Aflevering 1",
            "type": "video-long_form",
            "tags": [
              "Volledige Aflevering"
            ],
            "unpublishDate": 1654464240,
            "url": "https://www.goplay.be/video/culinaire-speurneuzen/culinaire-speurneuzen/culinaire-speurneuzen-s2-aflevering-1"
          },
          "pageUuid": "cf5cdfa7-cad8-425b-a69f-dad1230c3a46",
          "parentalRating": "AL",
          "path": "",
          "program": {
            "title": "Culinaire Speurneuzen",
            "poster": "https://wmimages.goplay.be/styles/d03a19a83908ec0fa0ca867113b0553c8bce6d67/2022-01/goplaympmpl1050x1500culinairespeurneuzen-r5e2vr.jpg?style=W3sicmVzaXplIjp7ImZpdCI6Imluc2lkZSIsIndpZHRoIjo0MDAsImhlaWdodCI6NTgwLCJ3aXRob3V0RW5sYXJnZW1lbnQiOmZhbHNlfX0seyJqcGVnIjp7InF1YWxpdHkiOjk1fX1d&sign=0b1531a5cdf973303368b2665fa1345b6c0ac4a07eadc4072a1ea9f28b98fbee",
            "category": "Lifestyle"
          },
          "seasonNumber": 2,
          "seekableFrom": 1641679772,
          "title": "Culinaire Speurneuzen - S2 - Aflevering 1",
          "tracking": {
            "item_name": "Culinaire Speurneuzen",
            "item_id": "ae1e96df-0e96-44e6-95d1-51e162898e43",
            "item_brand": "Play7",
            "item_category": "Lifestyle",
            "item_variant": "S2 - Aflevering 1"
          },
          "type": "long_form",
          "unpublishDate": 1654464240,
          "videoUuid": "ba01c210-232e-41f6-b765-34b034280b38",
          "whatsonId": "10126705726839527"
        },
        {
          "autoplay": false,
          "badge": "",
          "cimTag": "vid.tvi.ep.vod.free",
          "createdDate": 1642282920,
          "description": "<p>In de tweede reeks van Culinaire Speurneuzen worden per aflevering een aantal gepassioneerde mensen geportretteerd die elk op hun eigen manier op zoek gaan naar de beste producten uit hun vakgebied.</p>\r\n",
          "duration": 2123,
          "embedCta": null,
          "enablePreroll": true,
          "episodeNumber": 2,
          "episodeTitle": "S2 - Aflevering 2",
          "hasProductPlacement": false,
          "image": "https://wmimages.goplay.be/styles/1bada4e8211a435bbbf8f296e4d394b16b4d7b15/meta/vlcsnap-2022-01-13-20h10m42s659-r5nxa7.png?style=W3sicmVzaXplIjp7ImZpdCI6Imluc2lkZSIsIndpZHRoIjoxMjgwLCJoZWlnaHQiOjcyMCwid2l0aG91dEVubGFyZ2VtZW50Ijp0cnVlfX1d&sign=cc9f5fe022ff479314476e2d6afb132fdfff17d9d6b389e972416134bf732ba4",
          "isProtected": true,
          "isSeekable": false,
          "isStreaming": false,
          "link": "/video/culinaire-speurneuzen/culinaire-speurneuzen/culinaire-speurneuzen-s2-aflevering-2",
          "midrollOffsets": [
            769,
            1427
          ],
          "needs16PlusLabel": false,
          "pageInfo": {
            "author": "ken.ceulemans@sbsbelgium.be",
            "brand": "Play7",
            "description": "In de tweede reeks van Culinaire Speurneuzen worden per aflevering een aantal gepassioneerde mensen geportretteerd die elk op hun eigen manier op zoek gaan naar de beste producten uit hun vakgebied.",
            "nodeId": "23213",
            "nodeUuid": "7ff39693-201d-4151-8d6b-54f12e240295",
            "notificationsScore": 0,
            "program": "Culinaire Speurneuzen",
            "programKey": "culinaire_speurneuzen",
            "programId": "23069",
            "programUuid": "ae1e96df-0e96-44e6-95d1-51e162898e43",
            "publishDate": 1642282920,
            "title": "Culinaire Speurneuzen - S2 - Aflevering 2",
            "type": "video-long_form",
            "tags": [
              "Volledige Aflevering"
            ],
            "unpublishDate": 1654464780,
            "url": "https://www.goplay.be/video/culinaire-speurneuzen/culinaire-speurneuzen/culinaire-speurneuzen-s2-aflevering-2"
          },
          "pageUuid": "7ff39693-201d-4151-8d6b-54f12e240295",
          "parentalRating": "AL",
          "path": "",
          "program": {
            "title": "Culinaire Speurneuzen",
            "poster": "https://wmimages.goplay.be/styles/d03a19a83908ec0fa0ca867113b0553c8bce6d67/2022-01/goplaympmpl1050x1500culinairespeurneuzen-r5e2vr.jpg?style=W3sicmVzaXplIjp7ImZpdCI6Imluc2lkZSIsIndpZHRoIjo0MDAsImhlaWdodCI6NTgwLCJ3aXRob3V0RW5sYXJnZW1lbnQiOmZhbHNlfX0seyJqcGVnIjp7InF1YWxpdHkiOjk1fX1d&sign=0b1531a5cdf973303368b2665fa1345b6c0ac4a07eadc4072a1ea9f28b98fbee",
            "category": "Lifestyle"
          },
          "seasonNumber": 2,
          "seekableFrom": 1642285043,
          "title": "Culinaire Speurneuzen - S2 - Aflevering 2",
          "tracking": {
            "item_name": "Culinaire Speurneuzen",
            "item_id": "ae1e96df-0e96-44e6-95d1-51e162898e43",
            "item_brand": "Play7",
            "item_category": "Lifestyle",
            "item_variant": "S2 - Aflevering 2"
          },
          "type": "long_form",
          "unpublishDate": 1654464780,
          "videoUuid": "b577cbe0-ee22-40f2-9db1-60e7d882e0a8",
          "whatsonId": "10126705726870527"
        },
        {
          "autoplay": false,
          "badge": "",
          "cimTag": "vid.tvi.ep.vod.free",
          "createdDate": 1642887540,
          "description": "<p>In de tweede reeks van Culinaire Speurneuzen worden per aflevering een aantal gepassioneerde mensen geportretteerd die elk op hun eigen manier op zoek gaan naar de beste producten uit hun vakgebied.</p>\r\n",
          "duration": 2426,
          "embedCta": null,
          "enablePreroll": true,
          "episodeNumber": 3,
          "episodeTitle": "S2 - Aflevering 3",
          "hasProductPlacement": false,
          "image": "https://wmimages.goplay.be/styles/1bada4e8211a435bbbf8f296e4d394b16b4d7b15/meta/vlcsnap-2022-01-21-08h35m37s944.png?style=W3sicmVzaXplIjp7ImZpdCI6Imluc2lkZSIsIndpZHRoIjoxMjgwLCJoZWlnaHQiOjcyMCwid2l0aG91dEVubGFyZ2VtZW50Ijp0cnVlfX1d&sign=e016fc0beb3ef3ead42a2b8b7ac6ce1ee0f61a52b361a6c79144ba90d13e484a",
          "isProtected": true,
          "isSeekable": false,
          "isStreaming": false,
          "link": "/video/culinaire-speurneuzen/culinaire-speurneuzen/culinaire-speurneuzen-s2-aflevering-3",
          "midrollOffsets": [
            849,
            1774
          ],
          "needs16PlusLabel": false,
          "pageInfo": {
            "author": "ken.ceulemans@sbsbelgium.be",
            "brand": "Play7",
            "description": "In de tweede reeks van Culinaire Speurneuzen worden per aflevering een aantal gepassioneerde mensen geportretteerd die elk op hun eigen manier op zoek gaan naar de beste producten uit hun vakgebied.",
            "nodeId": "23371",
            "nodeUuid": "3310c16d-e338-4be2-964d-af28cc8ae594",
            "notificationsScore": 0,
            "program": "Culinaire Speurneuzen",
            "programKey": "culinaire_speurneuzen",
            "programId": "23069",
            "programUuid": "ae1e96df-0e96-44e6-95d1-51e162898e43",
            "publishDate": 1642887540,
            "title": "Culinaire Speurneuzen - S2 - Aflevering 3",
            "type": "video-long_form",
            "tags": [
              "Volledige Aflevering"
            ],
            "unpublishDate": 1654464960,
            "url": "https://www.goplay.be/video/culinaire-speurneuzen/culinaire-speurneuzen/culinaire-speurneuzen-s2-aflevering-3"
          },
          "pageUuid": "3310c16d-e338-4be2-964d-af28cc8ae594",
          "parentalRating": "AL",
          "path": "",
          "program": {
            "title": "Culinaire Speurneuzen",
            "poster": "https://wmimages.goplay.be/styles/d03a19a83908ec0fa0ca867113b0553c8bce6d67/2022-01/goplaympmpl1050x1500culinairespeurneuzen-r5e2vr.jpg?style=W3sicmVzaXplIjp7ImZpdCI6Imluc2lkZSIsIndpZHRoIjo0MDAsImhlaWdodCI6NTgwLCJ3aXRob3V0RW5sYXJnZW1lbnQiOmZhbHNlfX0seyJqcGVnIjp7InF1YWxpdHkiOjk1fX1d&sign=0b1531a5cdf973303368b2665fa1345b6c0ac4a07eadc4072a1ea9f28b98fbee",
            "category": "Lifestyle"
          },
          "seasonNumber": 2,
          "seekableFrom": 1642889966,
          "title": "Culinaire Speurneuzen - S2 - Aflevering 3",
          "tracking": {
            "item_name": "Culinaire Speurneuzen",
            "item_id": "ae1e96df-0e96-44e6-95d1-51e162898e43",
            "item_brand": "Play7",
            "item_category": "Lifestyle",
            "item_variant": "S2 - Aflevering 3"
          },
          "type": "long_form",
          "unpublishDate": 1654464960,
          "videoUuid": "cec3b05e-c09b-45da-aa13-9e4225fdbd27",
          "whatsonId": "10126705726881527"
        },
        {
          "autoplay": false,
          "badge": "",
          "cimTag": "vid.tvi.ep.vod.free",
          "createdDate": 1643492580,
          "description": "<p>In de tweede reeks van Culinaire Speurneuzen worden per aflevering een aantal gepassioneerde mensen geportretteerd die elk op hun eigen manier op zoek gaan naar de beste producten uit hun vakgebied.</p>\r\n",
          "duration": 2319,
          "embedCta": null,
          "enablePreroll": true,
          "episodeNumber": 4,
          "episodeTitle": "S2 - Aflevering 4",
          "hasProductPlacement": false,
          "image": "https://wmimages.goplay.be/styles/1bada4e8211a435bbbf8f296e4d394b16b4d7b15/meta/vlcsnap-2022-01-28-08h28m28s252.png?style=W3sicmVzaXplIjp7ImZpdCI6Imluc2lkZSIsIndpZHRoIjoxMjgwLCJoZWlnaHQiOjcyMCwid2l0aG91dEVubGFyZ2VtZW50Ijp0cnVlfX1d&sign=40bd3e469e045aeeb9315481689781f92b50ea11acc26e8e8266ca53be754337",
          "isProtected": true,
          "isSeekable": false,
          "isStreaming": false,
          "link": "/video/culinaire-speurneuzen/culinaire-speurneuzen/culinaire-speurneuzen-s2-aflevering-4",
          "midrollOffsets": [
            1,
            777,
            1622
          ],
          "needs16PlusLabel": false,
          "pageInfo": {
            "author": "ken.ceulemans@sbsbelgium.be",
            "brand": "Play7",
            "description": "In de tweede reeks van Culinaire Speurneuzen worden per aflevering een aantal gepassioneerde mensen geportretteerd die elk op hun eigen manier op zoek gaan naar de beste producten uit hun vakgebied.",
            "nodeId": "23535",
            "nodeUuid": "22bc03bd-1e11-48dc-988d-5703915dbda5",
            "notificationsScore": 0,
            "program": "Culinaire Speurneuzen",
            "programKey": "culinaire_speurneuzen",
            "programId": "23069",
            "programUuid": "ae1e96df-0e96-44e6-95d1-51e162898e43",
            "publishDate": 1643492580,
            "title": "Culinaire Speurneuzen - S2 - Aflevering 4",
            "type": "video-long_form",
            "tags": [
              "Volledige Aflevering"
            ],
            "unpublishDate": 1654464960,
            "url": "https://www.goplay.be/video/culinaire-speurneuzen/culinaire-speurneuzen/culinaire-speurneuzen-s2-aflevering-4"
          },
          "pageUuid": "22bc03bd-1e11-48dc-988d-5703915dbda5",
          "parentalRating": "AL",
          "path": "",
          "program": {
            "title": "Culinaire Speurneuzen",
            "poster": "https://wmimages.goplay.be/styles/d03a19a83908ec0fa0ca867113b0553c8bce6d67/2022-01/goplaympmpl1050x1500culinairespeurneuzen-r5e2vr.jpg?style=W3sicmVzaXplIjp7ImZpdCI6Imluc2lkZSIsIndpZHRoIjo0MDAsImhlaWdodCI6NTgwLCJ3aXRob3V0RW5sYXJnZW1lbnQiOmZhbHNlfX0seyJqcGVnIjp7InF1YWxpdHkiOjk1fX1d&sign=0b1531a5cdf973303368b2665fa1345b6c0ac4a07eadc4072a1ea9f28b98fbee",
            "category": "Lifestyle"
          },
          "seasonNumber": 2,
          "seekableFrom": 1643494899,
          "title": "Culinaire Speurneuzen - S2 - Aflevering 4",
          "tracking": {
            "item_name": "Culinaire Speurneuzen",
            "item_id": "ae1e96df-0e96-44e6-95d1-51e162898e43",
            "item_brand": "Play7",
            "item_category": "Lifestyle",
            "item_variant": "S2 - Aflevering 4"
          },
          "type": "long_form",
          "unpublishDate": 1654464960,
          "videoUuid": "84c8bcf5-07d2-4d00-ad26-4dcbf676b98f",
          "whatsonId": "10126705726892527"
        },
        {
          "autoplay": false,
          "badge": "",
          "cimTag": "vid.tvi.ep.vod.free",
          "createdDate": 1644096840,
          "description": "<p>In de tweede reeks van Culinaire Speurneuzen worden per aflevering een aantal gepassioneerde mensen geportretteerd die elk op hun eigen manier op zoek gaan naar de beste producten uit hun vakgebied.</p>\r\n",
          "duration": 2354,
          "embedCta": null,
          "enablePreroll": true,
          "episodeNumber": 5,
          "episodeTitle": "S2 - Aflevering 5",
          "hasProductPlacement": false,
          "image": "https://wmimages.goplay.be/styles/1bada4e8211a435bbbf8f296e4d394b16b4d7b15/meta/vlcsnap-2022-02-04-08h54m43s673.png?style=W3sicmVzaXplIjp7ImZpdCI6Imluc2lkZSIsIndpZHRoIjoxMjgwLCJoZWlnaHQiOjcyMCwid2l0aG91dEVubGFyZ2VtZW50Ijp0cnVlfX1d&sign=62458d22ea2ba5ec91f3f61a28cd9133a952019c2b39ad3945f62fd215bd988e",
          "isProtected": true,
          "isSeekable": false,
          "isStreaming": false,
          "link": "/video/culinaire-speurneuzen/culinaire-speurneuzen/culinaire-speurneuzen-s2-aflevering-5",
          "midrollOffsets": [
            742,
            1558
          ],
          "needs16PlusLabel": false,
          "pageInfo": {
            "author": "ken.ceulemans@sbsbelgium.be",
            "brand": "Play7",
            "description": "In de tweede reeks van Culinaire Speurneuzen worden per aflevering een aantal gepassioneerde mensen geportretteerd die elk op hun eigen manier op zoek gaan naar de beste producten uit hun vakgebied.",
            "nodeId": "23676",
            "nodeUuid": "d6a6ea0b-a3f5-4779-bf2d-228321aa2072",
            "notificationsScore": 0,
            "program": "Culinaire Speurneuzen",
            "programKey": "culinaire_speurneuzen",
            "programId": "23069",
            "programUuid": "ae1e96df-0e96-44e6-95d1-51e162898e43",
            "publishDate": 1644096840,
            "title": "Culinaire Speurneuzen - S2 - Aflevering 5",
            "type": "video-long_form",
            "tags": [
              "Volledige Aflevering"
            ],
            "unpublishDate": 1654464480,
            "url": "https://www.goplay.be/video/culinaire-speurneuzen/culinaire-speurneuzen/culinaire-speurneuzen-s2-aflevering-5"
          },
          "pageUuid": "d6a6ea0b-a3f5-4779-bf2d-228321aa2072",
          "parentalRating": "AL",
          "path": "",
          "program": {
            "title": "Culinaire Speurneuzen",
            "poster": "https://wmimages.goplay.be/styles/d03a19a83908ec0fa0ca867113b0553c8bce6d67/2022-01/goplaympmpl1050x1500culinairespeurneuzen-r5e2vr.jpg?style=W3sicmVzaXplIjp7ImZpdCI6Imluc2lkZSIsIndpZHRoIjo0MDAsImhlaWdodCI6NTgwLCJ3aXRob3V0RW5sYXJnZW1lbnQiOmZhbHNlfX0seyJqcGVnIjp7InF1YWxpdHkiOjk1fX1d&sign=0b1531a5cdf973303368b2665fa1345b6c0ac4a07eadc4072a1ea9f28b98fbee",
            "category": "Lifestyle"
          },
          "seasonNumber": 2,
          "seekableFrom": 1644099194,
          "title": "Culinaire Speurneuzen - S2 - Aflevering 5",
          "tracking": {
            "item_name": "Culinaire Speurneuzen",
            "item_id": "ae1e96df-0e96-44e6-95d1-51e162898e43",
            "item_brand": "Play7",
            "item_category": "Lifestyle",
            "item_variant": "S2 - Aflevering 5"
          },
          "type": "long_form",
          "unpublishDate": 1654464480,
          "videoUuid": "17edb132-0259-463c-a5c4-eedd1e1375ed",
          "whatsonId": "10126705726903527"
        }
      ],
      "id": "1d17e45c-b489-4ef7-95a8-9b2656cf89aa",
      "link": "/video/culinaire-speurneuzen/culinaire-speurneuzen",
      "pageInfo": {
        "author": "Axel.Florizoone@sbsbelgium.be",
        "brand": "Play7",
        "description": "In de tweede reeks van Culinaire Speurneuzen worden per aflevering een aantal gepassioneerde mensen geportretteerd die elk op hun eigen manier op zoek gaan naar de beste producten uit hun vakgebied.",
        "nodeId": "23070",
        "nodeUuid": "1d17e45c-b489-4ef7-95a8-9b2656cf89aa",
        "notificationsScore": 0,
        "program": "Culinaire Speurneuzen",
        "programKey": "culinaire_speurneuzen",
        "programId": "23069",
        "programUuid": "ae1e96df-0e96-44e6-95d1-51e162898e43",
        "publishDate": 1641677580,
        "title": "Culinaire Speurneuzen (S2)",
        "type": "playlist",
        "tags": [],
        "unpublishDate": 0,
        "url": "https://www.goplay.be/video/culinaire-speurneuzen/culinaire-speurneuzen"
      },
      "title": "Seizoen 2"
    }
  ],
  "social": {
    "facebook": "",
    "hashtag": "",
    "instagram": "",
    "twitter": ""
  },
  "tracking": {
    "item_name": "Culinaire Speurneuzen",
    "item_id": "ae1e96df-0e96-44e6-95d1-51e162898e43",
    "item_brand": "Play7",
    "item_category": "Lifestyle"
  },
  "movie": null,
  "streamz": []
}


    """

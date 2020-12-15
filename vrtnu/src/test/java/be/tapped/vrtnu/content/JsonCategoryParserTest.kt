package be.tapped.vrtnu.content

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

public class JsonCategoryParserTest : StringSpec() {

    init {
        "should be able to parse" {
            val categories = JsonCategoryParser().parse(categories).orNull()!!
            categories shouldHaveSize 19
            categories.map(Category::name) shouldBe listOf(
                "met-audiodescriptie",
                "cultuur",
                "docu",
                "entertainment",
                "films",
                "human-interest",
                "humor",
                "voor-kinderen",
                "koken",
                "levensbeschouwing",
                "lifestyle",
                "muziek",
                "nieuws-en-actua",
                "nostalgie",
                "series",
                "sport",
                "talkshows",
                "met-gebarentaal",
                "wetenschap-en-natuur",
            )
        }
    }

    private companion object {
        private const val categories =
            """
        {
  "hiddenInApp": false,
  "selectedTags": [],
  "items": [
    {
      "imageStoreUrl": "//images.vrt.be/orig/2016/10/03/de141920-8965-11e6-aef1-00163edf48dd.jpg",
      "name": "met-audiodescriptie",
      "modelUri": "/vrtnu/categorieen/met-audiodescriptie.model.json",
      "title": "Audiodescriptie",
      "link": "/vrtnu/categorieen/met-audiodescriptie.html",
      "thumbnailUrl": "",
      "image": {
        "src": "//images.vrt.be/orig/2016/10/03/de141920-8965-11e6-aef1-00163edf48dd.jpg",
        "srcUriTemplate": "//images.vrt.be/orig/2016/10/03/de141920-8965-11e6-aef1-00163edf48dd.jpg",
        "focalPoint": "50% 50%",
        "id": "image",
        "hiddenInApp": false,
        ":type": "vrtvideo/components/content/image"
      },
      "description": "Kijk en luister online naar toegankelijke VRT-programma's met audiodescriptie via de VRT NU app en site.",
      "reference": {
        "link": "/vrtnu/categorieen/met-audiodescriptie.html",
        "modelUri": "/vrtnu/categorieen/met-audiodescriptie.model.json",
        "referenceType": "internal"
      },
      "actions": [],
      ":type": "vrtvideo/components/structure/page-category"
    },
    {
      "imageStoreUrl": "//images.vrt.be/orig/2020/11/16/8902ae62-2824-11eb-aae0-02b7b76bf47f.jpg",
      "name": "cultuur",
      "permalink": "https://vrtnu.page.link/NpGkxKCqPBXvy7vT8",
      "modelUri": "/vrtnu/categorieen/cultuur.model.json",
      "title": "Cultuur",
      "link": "/vrtnu/categorieen/cultuur.html",
      "thumbnailUrl": "",
      "image": {
        "src": "//images.vrt.be/orig/2020/11/16/8902ae62-2824-11eb-aae0-02b7b76bf47f.jpg",
        "srcUriTemplate": "//images.vrt.be/orig/2020/11/16/8902ae62-2824-11eb-aae0-02b7b76bf47f.jpg",
        "focalPoint": "50% 50%",
        "id": "image",
        "hiddenInApp": false,
        ":type": "vrtvideo/components/content/image"
      },
      "description": "Kijk gratis online naar cultuurprogramma's met VRT NU via de site of app.",
      "reference": {
        "link": "/vrtnu/categorieen/cultuur.html",
        "modelUri": "/vrtnu/categorieen/cultuur.model.json",
        "permalink": "https://vrtnu.page.link/NpGkxKCqPBXvy7vT8",
        "referenceType": "internal"
      },
      "actions": [],
      ":type": "vrtvideo/components/structure/page-category"
    },
    {
      "imageStoreUrl": "//images.vrt.be/orig/2020/11/18/bcafcbc9-29bc-11eb-aae0-02b7b76bf47f.jpg",
      "name": "docu",
      "permalink": "https://vrtnu.page.link/DnzkesH9H7uGhSxV7",
      "modelUri": "/vrtnu/categorieen/docu.model.json",
      "title": "Docu",
      "link": "/vrtnu/categorieen/docu.html",
      "thumbnailUrl": "",
      "image": {
        "src": "//images.vrt.be/orig/2020/11/18/bcafcbc9-29bc-11eb-aae0-02b7b76bf47f.jpg",
        "alt": "Vierdelige documentairereeks die ons laat kennismaken met het Instituut voor Tropische Geneeskunde in Antwerpen en zijn onderzoek naar allerhande ziektekiemen.",
        "srcUriTemplate": "//images.vrt.be/orig/2020/11/18/bcafcbc9-29bc-11eb-aae0-02b7b76bf47f.jpg",
        "focalPoint": "50% 50%",
        "id": "image",
        "hiddenInApp": false,
        ":type": "vrtvideo/components/content/image"
      },
      "description": "Kijk gratis online boeiende documentaires en reportages zoals Pano met VRT NU via de site of app.",
      "reference": {
        "link": "/vrtnu/categorieen/docu.html",
        "modelUri": "/vrtnu/categorieen/docu.model.json",
        "permalink": "https://vrtnu.page.link/DnzkesH9H7uGhSxV7",
        "referenceType": "internal"
      },
      "actions": [],
      ":type": "vrtvideo/components/structure/page-category"
    },
    {
      "imageStoreUrl": "//images.vrt.be/orig/2020/11/07/42168d27-214b-11eb-aae0-02b7b76bf47f.jpg",
      "name": "entertainment",
      "permalink": "https://vrtnu.page.link/P2CF2o8a4E39GJBw5",
      "modelUri": "/vrtnu/categorieen/entertainment.model.json",
      "title": "Entertainment",
      "link": "/vrtnu/categorieen/entertainment.html",
      "thumbnailUrl": "",
      "image": {
        "src": "//images.vrt.be/orig/2020/11/07/42168d27-214b-11eb-aae0-02b7b76bf47f.jpg",
        "alt": "Philippe Geubels kruist de degens met onze noorderburen, om te lachen met hen en hun gekke gewoontes. Op zijn gekende humoristische toon vertelt Philippe in een stand-upshow over alle vooroordelen over onze buren, over Hollandse tradities en eigenaardigheden.",
        "srcUriTemplate": "//images.vrt.be/orig/2020/11/07/42168d27-214b-11eb-aae0-02b7b76bf47f.jpg",
        "focalPoint": "50% 50%",
        "id": "image",
        "hiddenInApp": false,
        ":type": "vrtvideo/components/content/image"
      },
      "description": "Kijk gratis online naar je favoriete entertainmentprogramma's met VRT NU via de site of app.",
      "reference": {
        "link": "/vrtnu/categorieen/entertainment.html",
        "modelUri": "/vrtnu/categorieen/entertainment.model.json",
        "permalink": "https://vrtnu.page.link/P2CF2o8a4E39GJBw5",
        "referenceType": "internal"
      },
      "actions": [],
      ":type": "vrtvideo/components/structure/page-category"
    },
    {
      "imageStoreUrl": "//images.vrt.be/orig/2019/10/10/857c5c0e-eb3e-11e9-abcc-02b7b76bf47f.jpg",
      "name": "films",
      "modelUri": "/vrtnu/categorieen/films.model.json",
      "title": "Film",
      "link": "/vrtnu/categorieen/films.html",
      "thumbnailUrl": "",
      "image": {
        "src": "//images.vrt.be/orig/2019/10/10/857c5c0e-eb3e-11e9-abcc-02b7b76bf47f.jpg",
        "srcUriTemplate": "//images.vrt.be/orig/2019/10/10/857c5c0e-eb3e-11e9-abcc-02b7b76bf47f.jpg",
        "focalPoint": "50% 50%",
        "id": "image",
        "hiddenInApp": false,
        ":type": "vrtvideo/components/content/image"
      },
      "description": "Kijk gratis online naar je favoriete films met VRT NU via de site of app.",
      "reference": {
        "link": "/vrtnu/categorieen/films.html",
        "modelUri": "/vrtnu/categorieen/films.model.json",
        "referenceType": "internal"
      },
      "actions": [],
      ":type": "vrtvideo/components/structure/page-category"
    },
    {
      "imageStoreUrl": "//images.vrt.be/orig/2020/11/03/e85c8723-1e10-11eb-aae0-02b7b76bf47f.jpg",
      "name": "human-interest",
      "permalink": "https://vrtnu.page.link/WUAhbN2r9fK4doik7",
      "modelUri": "/vrtnu/categorieen/human-interest.model.json",
      "title": "Human interest",
      "link": "/vrtnu/categorieen/human-interest.html",
      "thumbnailUrl": "",
      "image": {
        "src": "//images.vrt.be/orig/2020/11/03/e85c8723-1e10-11eb-aae0-02b7b76bf47f.jpg",
        "alt": "Documentaire realityreeks waarin Eric Goens een bekende gast zijn of haar ziel laat blootleggen in een huis aan de rand van het bos, afgesloten van hun gewone leefwereld.",
        "srcUriTemplate": "//images.vrt.be/orig/2020/11/03/e85c8723-1e10-11eb-aae0-02b7b76bf47f.jpg",
        "focalPoint": "50% 0%",
        "id": "image",
        "hiddenInApp": false,
        ":type": "vrtvideo/components/content/image"
      },
      "description": "Joris Hessels en Dominique Van Malder gaan dit najaar opnieuw op pad met hun zelfgebouwde mobiele radiostudio. Ze strijken neer op acht bijzondere locaties, maken er hyperlokale radio en luisteren naar diepmenselijke verhalen die balanceren tussen vreugde en verdriet, humor en ontroering. Interviews met, optredens van en portretten over plaatselijke bewoners wisselen af met muzikale verzoeken. En tussendoor zien we hoe Radio Gaga meeleeft op het ritme van onder meer een voorziening voor blinden, een abdij, Benidorm, een gevangenis, palliatieve zorg en het Antwerpse stadsdeel Linkeroever. Radio Gaga is er dit najaar ook op Radio 2 én er komt een Radio Gaga-boek. Vanaf dinsdag 4 oktober om 21.00 u.",
      "reference": {
        "link": "/vrtnu/categorieen/human-interest.html",
        "modelUri": "/vrtnu/categorieen/human-interest.model.json",
        "permalink": "https://vrtnu.page.link/WUAhbN2r9fK4doik7",
        "referenceType": "internal"
      },
      "actions": [],
      ":type": "vrtvideo/components/structure/page-category"
    },
    {
      "imageStoreUrl": "//images.vrt.be/orig/2020/09/02/51fc23a0-ed1b-11ea-aae0-02b7b76bf47f.jpg",
      "name": "humor",
      "permalink": "https://vrtnu.page.link/3qLixvtT33RgETuE7",
      "modelUri": "/vrtnu/categorieen/humor.model.json",
      "title": "Humor",
      "link": "/vrtnu/categorieen/humor.html",
      "thumbnailUrl": "",
      "image": {
        "src": "//images.vrt.be/orig/2020/09/02/51fc23a0-ed1b-11ea-aae0-02b7b76bf47f.jpg",
        "srcUriTemplate": "//images.vrt.be/orig/2020/09/02/51fc23a0-ed1b-11ea-aae0-02b7b76bf47f.jpg",
        "focalPoint": "50% 50%",
        "id": "image",
        "hiddenInApp": false,
        ":type": "vrtvideo/components/content/image"
      },
      "description": "Vanaf 5 januari is De Ideale Wereld te zien op Canvas.",
      "reference": {
        "link": "/vrtnu/categorieen/humor.html",
        "modelUri": "/vrtnu/categorieen/humor.model.json",
        "permalink": "https://vrtnu.page.link/3qLixvtT33RgETuE7",
        "referenceType": "internal"
      },
      "actions": [],
      ":type": "vrtvideo/components/structure/page-category"
    },
    {
      "imageStoreUrl": "//images.vrt.be/orig/2020/09/02/86d13982-ed1b-11ea-aae0-02b7b76bf47f.jpg",
      "name": "voor-kinderen",
      "permalink": "https://vrtnu.page.link/2mjC8hzgWwy6wqXf6",
      "modelUri": "/vrtnu/categorieen/voor-kinderen.model.json",
      "title": "Kinderen en jongeren",
      "link": "/vrtnu/categorieen/voor-kinderen.html",
      "thumbnailUrl": "",
      "image": {
        "src": "//images.vrt.be/orig/2020/09/02/86d13982-ed1b-11ea-aae0-02b7b76bf47f.jpg",
        "srcUriTemplate": "//images.vrt.be/orig/2020/09/02/86d13982-ed1b-11ea-aae0-02b7b76bf47f.jpg",
        "focalPoint": "50% 50%",
        "id": "image",
        "hiddenInApp": false,
        ":type": "vrtvideo/components/content/image"
      },
      "description": "Kijk gratis online naar je favoriete programma, zoals #likeme en Kaatje, met VRT NU via de site of app.",
      "reference": {
        "link": "/vrtnu/categorieen/voor-kinderen.html",
        "modelUri": "/vrtnu/categorieen/voor-kinderen.model.json",
        "permalink": "https://vrtnu.page.link/2mjC8hzgWwy6wqXf6",
        "referenceType": "internal"
      },
      "actions": [],
      ":type": "vrtvideo/components/structure/page-category"
    },
    {
      "imageStoreUrl": "//images.vrt.be/orig/2019/08/22/4c313980-c4c0-11e9-abcc-02b7b76bf47f.jpg",
      "name": "koken",
      "modelUri": "/vrtnu/categorieen/koken.model.json",
      "title": "Koken",
      "link": "/vrtnu/categorieen/koken.html",
      "thumbnailUrl": "",
      "image": {
        "src": "//images.vrt.be/orig/2019/08/22/4c313980-c4c0-11e9-abcc-02b7b76bf47f.jpg",
        "alt": "Jeroen Meus in de nieuwe keuken van Dagelijkse kost.",
        "srcUriTemplate": "//images.vrt.be/orig/2019/08/22/4c313980-c4c0-11e9-abcc-02b7b76bf47f.jpg",
        "focalPoint": "50% 50%",
        "id": "image",
        "hiddenInApp": false,
        ":type": "vrtvideo/components/content/image"
      },
      "description": "Kijk gratis online naar je favoriete kookprogramma’s zoals Dagelijkse Kost met VRT NU via de site of app.",
      "reference": {
        "link": "/vrtnu/categorieen/koken.html",
        "modelUri": "/vrtnu/categorieen/koken.model.json",
        "referenceType": "internal"
      },
      "actions": [],
      ":type": "vrtvideo/components/structure/page-category"
    },
    {
      "imageStoreUrl": "//images.vrt.be/orig/2019/05/21/f68be9d1-7bcc-11e9-abcc-02b7b76bf47f.jpg",
      "name": "levensbeschouwing",
      "modelUri": "/vrtnu/categorieen/levensbeschouwing.model.json",
      "title": "Levensbeschouwing",
      "link": "/vrtnu/categorieen/levensbeschouwing.html",
      "thumbnailUrl": "",
      "image": {
        "src": "//images.vrt.be/orig/2019/05/21/f68be9d1-7bcc-11e9-abcc-02b7b76bf47f.jpg",
        "srcUriTemplate": "//images.vrt.be/orig/2019/05/21/f68be9d1-7bcc-11e9-abcc-02b7b76bf47f.jpg",
        "focalPoint": "50% 50%",
        "id": "image",
        "hiddenInApp": false,
        ":type": "vrtvideo/components/content/image"
      },
      "reference": {
        "link": "/vrtnu/categorieen/levensbeschouwing.html",
        "modelUri": "/vrtnu/categorieen/levensbeschouwing.model.json",
        "referenceType": "internal"
      },
      "actions": [],
      ":type": "vrtvideo/components/structure/page-category"
    },
    {
      "imageStoreUrl": "//images.vrt.be/orig/2020/09/06/b24eb785-f07e-11ea-aae0-02b7b76bf47f.jpg",
      "name": "lifestyle",
      "permalink": "https://vrtnu.page.link/sVf76rWmo2vSE92B6",
      "modelUri": "/vrtnu/categorieen/lifestyle.model.json",
      "title": "Lifestyle",
      "link": "/vrtnu/categorieen/lifestyle.html",
      "thumbnailUrl": "",
      "image": {
        "src": "//images.vrt.be/orig/2020/09/06/b24eb785-f07e-11ea-aae0-02b7b76bf47f.jpg",
        "srcUriTemplate": "//images.vrt.be/orig/2020/09/06/b24eb785-f07e-11ea-aae0-02b7b76bf47f.jpg",
        "focalPoint": "50% 50%",
        "id": "image",
        "hiddenInApp": false,
        ":type": "vrtvideo/components/content/image"
      },
      "description": "Kijk gratis online naar je favoriete lifestyleprogramma's met VRT NU via de site of app.",
      "reference": {
        "link": "/vrtnu/categorieen/lifestyle.html",
        "modelUri": "/vrtnu/categorieen/lifestyle.model.json",
        "permalink": "https://vrtnu.page.link/sVf76rWmo2vSE92B6",
        "referenceType": "internal"
      },
      "actions": [],
      ":type": "vrtvideo/components/structure/page-category"
    },
    {
      "imageStoreUrl": "//images.vrt.be/orig/2019/12/13/74be7c86-1d93-11ea-abcc-02b7b76bf47f.jpg",
      "name": "muziek",
      "permalink": "https://vrtnu.page.link/jAs3SfhhzrXkAjdMA",
      "modelUri": "/vrtnu/categorieen/muziek.model.json",
      "title": "Muziek",
      "link": "/vrtnu/categorieen/muziek.html",
      "thumbnailUrl": "",
      "image": {
        "src": "//images.vrt.be/orig/2019/12/13/74be7c86-1d93-11ea-abcc-02b7b76bf47f.jpg",
        "srcUriTemplate": "//images.vrt.be/orig/2019/12/13/74be7c86-1d93-11ea-abcc-02b7b76bf47f.jpg",
        "focalPoint": "50% 0%",
        "id": "image",
        "hiddenInApp": false,
        ":type": "vrtvideo/components/content/image"
      },
      "reference": {
        "link": "/vrtnu/categorieen/muziek.html",
        "modelUri": "/vrtnu/categorieen/muziek.model.json",
        "permalink": "https://vrtnu.page.link/jAs3SfhhzrXkAjdMA",
        "referenceType": "internal"
      },
      "actions": [],
      ":type": "vrtvideo/components/structure/page-category"
    },
    {
      "imageStoreUrl": "//images.vrt.be/orig/2020/11/18/ddcf2cb5-29ba-11eb-aae0-02b7b76bf47f.jpg",
      "name": "nieuws-en-actua",
      "permalink": "https://vrtnu.page.link/pGrw9TocVyVBCyL98",
      "modelUri": "/vrtnu/categorieen/nieuws-en-actua.model.json",
      "title": "Nieuws en actua",
      "link": "/vrtnu/categorieen/nieuws-en-actua.html",
      "thumbnailUrl": "",
      "image": {
        "src": "//images.vrt.be/orig/2020/11/18/ddcf2cb5-29ba-11eb-aae0-02b7b76bf47f.jpg",
        "srcUriTemplate": "//images.vrt.be/orig/2020/11/18/ddcf2cb5-29ba-11eb-aae0-02b7b76bf47f.jpg",
        "focalPoint": "50% 50%",
        "id": "image",
        "hiddenInApp": false,
        ":type": "vrtvideo/components/content/image"
      },
      "description": "Kijk gratis online naar Nieuws en actua-programma's van VRT NWS, zoals Terzake en De Afspraak, met VRT NU via de site en app.",
      "reference": {
        "link": "/vrtnu/categorieen/nieuws-en-actua.html",
        "modelUri": "/vrtnu/categorieen/nieuws-en-actua.model.json",
        "permalink": "https://vrtnu.page.link/pGrw9TocVyVBCyL98",
        "referenceType": "internal"
      },
      "actions": [],
      ":type": "vrtvideo/components/structure/page-category"
    },
    {
      "imageStoreUrl": "//images.vrt.be/orig/2016/04/29/f2c4e5f1-0de9-11e6-8682-00163edf843f.jpg",
      "name": "nostalgie",
      "permalink": "https://vrtnu.page.link/B97jnh1AVjPPHvyT8",
      "modelUri": "/vrtnu/categorieen/nostalgie.model.json",
      "title": "Nostalgie",
      "link": "/vrtnu/categorieen/nostalgie.html",
      "thumbnailUrl": "",
      "image": {
        "src": "//images.vrt.be/orig/2016/04/29/f2c4e5f1-0de9-11e6-8682-00163edf843f.jpg",
        "alt": "jeugdreeks",
        "srcUriTemplate": "//images.vrt.be/orig/2016/04/29/f2c4e5f1-0de9-11e6-8682-00163edf843f.jpg",
        "focalPoint": "50% 50%",
        "id": "image",
        "hiddenInApp": false,
        ":type": "vrtvideo/components/content/image"
      },
      "reference": {
        "link": "/vrtnu/categorieen/nostalgie.html",
        "modelUri": "/vrtnu/categorieen/nostalgie.model.json",
        "permalink": "https://vrtnu.page.link/B97jnh1AVjPPHvyT8",
        "referenceType": "internal"
      },
      "actions": [],
      ":type": "vrtvideo/components/structure/page-category"
    },
    {
      "imageStoreUrl": "//images.vrt.be/orig/2018/12/28/990e4161-0ad9-11e9-abcc-02b7b76bf47f.jpg",
      "name": "series",
      "permalink": "https://vrtnu.page.link/CbXT8jUpzUNjabTC6",
      "modelUri": "/vrtnu/categorieen/series.model.json",
      "title": "Series",
      "link": "/vrtnu/categorieen/series.html",
      "thumbnailUrl": "",
      "image": {
        "src": "//images.vrt.be/orig/2018/12/28/990e4161-0ad9-11e9-abcc-02b7b76bf47f.jpg",
        "alt": "Voor alle ItaliÃ«fans en liefhebbers van intens drama heeft Canvas een geweldig kerstcadeau klaarliggen: de HBO-verfilming van My Brilliant Friend, de bejubelde roman van Elena Ferrante. My Brilliant Friend (LâAmica geniale - De geniale vriendin) is de eerste van haar vier âNapolitaanse romansâ over de levenslange vriendschap tussen twee Italiaanse vrouwen, van hun kindertijd in het Napels van de jaren '50 tot 2014.\n\nVoor de tv-adaptatie van dit prachtige Italiaanse verhaal sloeg HBO de handen in elkaar met de Italiaanse openbare omroep RAI. My Brilliant Friend werd opgenomen in het Italiaans, deels zelfs in het Napolitaans, en is daarmee de eerste niet-Engelstalige HBO-productie.\n\nDe reeks werd bij de avant-premiÃ¨re op het Filmfestival van VenetiÃ« onthaald op een staande ovatie. Pers en publiek waren in de wolken over de nauwgezette adaptatie van het verhaal, de fantastische vertolkingen van de jonge acteurs, de sfeerschepping en algemene âproduction valuesâ. Mede daardoor wordt de serie nu al vergeleken met die andere legendarische succesreeks La meglio gioventuÌ. -\nCanvas zendt My Brilliant Friend uit in de kerstvakantie, vanaf dinsdag 25 december, telkens op dinsdag, woensdag, donderdag en vrijdag om 22.00 uur. Wie niet kan wachten op de volgende afleveringen kan ze vanaf 25 december ook al allemaal bekijken op VRT NU.",
        "srcUriTemplate": "//images.vrt.be/orig/2018/12/28/990e4161-0ad9-11e9-abcc-02b7b76bf47f.jpg",
        "focalPoint": "50% 50%",
        "id": "image",
        "hiddenInApp": false,
        ":type": "vrtvideo/components/content/image"
      },
      "description": "Kijk gratis online naar je favoriete vlaamse en buitenlandse series met VRT NU via de site of app.",
      "reference": {
        "link": "/vrtnu/categorieen/series.html",
        "modelUri": "/vrtnu/categorieen/series.model.json",
        "permalink": "https://vrtnu.page.link/CbXT8jUpzUNjabTC6",
        "referenceType": "internal"
      },
      "actions": [],
      ":type": "vrtvideo/components/structure/page-category"
    },
    {
      "imageStoreUrl": "//images.vrt.be/orig/2020/01/02/a9ec46c4-2d7c-11ea-abcc-02b7b76bf47f.jpg",
      "name": "sport",
      "permalink": "https://vrtnu.page.link/bfj37oPcH1cDx23u7",
      "modelUri": "/vrtnu/categorieen/sport.model.json",
      "title": "Sport",
      "link": "/vrtnu/categorieen/sport.html",
      "thumbnailUrl": "",
      "image": {
        "src": "//images.vrt.be/orig/2020/01/02/a9ec46c4-2d7c-11ea-abcc-02b7b76bf47f.jpg",
        "alt": "Voor de derde winter op rij geeft DNA Nys een blik achter de schermen bij de bekendste veldritfamilie van het moment: de familie Nys. Oud-veldrit kampioen Sven beleeft vanop de eerste rij de prille stappen in de carriÃ¨re van zijn zoon Thibau. Die is nu tweedejaars junior, een jaar waarin verwacht wordt dat hij zowat alles zal winnen. Maar lukt hem dat ook? En hoe gaat hij met die druk om? Hoe reageert hij op de steeds groter wordende aandacht?",
        "srcUriTemplate": "//images.vrt.be/orig/2020/01/02/a9ec46c4-2d7c-11ea-abcc-02b7b76bf47f.jpg",
        "focalPoint": "50% 50%",
        "id": "image",
        "hiddenInApp": false,
        ":type": "vrtvideo/components/content/image"
      },
      "reference": {
        "link": "/vrtnu/categorieen/sport.html",
        "modelUri": "/vrtnu/categorieen/sport.model.json",
        "permalink": "https://vrtnu.page.link/bfj37oPcH1cDx23u7",
        "referenceType": "internal"
      },
      "actions": [],
      ":type": "vrtvideo/components/structure/page-category"
    },
    {
      "imageStoreUrl": "//images.vrt.be/orig/2020/11/10/fbcb28b3-2360-11eb-aae0-02b7b76bf47f.jpg",
      "name": "talkshows",
      "permalink": "https://vrtnu.page.link/TnhejGpa3bVwdQ3DA",
      "modelUri": "/vrtnu/categorieen/talkshows.model.json",
      "title": "Talkshows",
      "link": "/vrtnu/categorieen/talkshows.html",
      "thumbnailUrl": "",
      "image": {
        "src": "//images.vrt.be/orig/2020/11/10/fbcb28b3-2360-11eb-aae0-02b7b76bf47f.jpg",
        "alt": "Dagelijks actuaprogramma waarin Bart Schols gasten ontvangt en op eigen manier naar het nieuws van de dag en de actualiteit kijkt.",
        "srcUriTemplate": "//images.vrt.be/orig/2020/11/10/fbcb28b3-2360-11eb-aae0-02b7b76bf47f.jpg",
        "focalPoint": "50% 50%",
        "id": "image",
        "hiddenInApp": false,
        ":type": "vrtvideo/components/content/image"
      },
      "reference": {
        "link": "/vrtnu/categorieen/talkshows.html",
        "modelUri": "/vrtnu/categorieen/talkshows.model.json",
        "permalink": "https://vrtnu.page.link/TnhejGpa3bVwdQ3DA",
        "referenceType": "internal"
      },
      "actions": [],
      ":type": "vrtvideo/components/structure/page-category"
    },
    {
      "imageStoreUrl": "//images.vrt.be/orig/2016/10/03/d9f4f3ec-8965-11e6-aef1-00163edf48dd.jpg",
      "name": "met-gebarentaal",
      "modelUri": "/vrtnu/categorieen/met-gebarentaal.model.json",
      "title": "Vlaamse Gebarentaal",
      "link": "/vrtnu/categorieen/met-gebarentaal.html",
      "thumbnailUrl": "",
      "image": {
        "src": "//images.vrt.be/orig/2016/10/03/d9f4f3ec-8965-11e6-aef1-00163edf48dd.jpg",
        "srcUriTemplate": "//images.vrt.be/orig/2016/10/03/d9f4f3ec-8965-11e6-aef1-00163edf48dd.jpg",
        "focalPoint": "50% 50%",
        "id": "image",
        "hiddenInApp": false,
        ":type": "vrtvideo/components/content/image"
      },
      "reference": {
        "link": "/vrtnu/categorieen/met-gebarentaal.html",
        "modelUri": "/vrtnu/categorieen/met-gebarentaal.model.json",
        "referenceType": "internal"
      },
      "actions": [],
      ":type": "vrtvideo/components/structure/page-category"
    },
    {
      "imageStoreUrl": "//images.vrt.be/orig/2020/09/28/294a2c15-018f-11eb-aae0-02b7b76bf47f.jpg",
      "name": "wetenschap-en-natuur",
      "permalink": "https://vrtnu.page.link/UrW4TZWcy2sYN1Tj9",
      "modelUri": "/vrtnu/categorieen/wetenschap-en-natuur.model.json",
      "title": "Wetenschap & natuur",
      "link": "/vrtnu/categorieen/wetenschap-en-natuur.html",
      "thumbnailUrl": "",
      "image": {
        "src": "//images.vrt.be/orig/2020/09/28/294a2c15-018f-11eb-aae0-02b7b76bf47f.jpg",
        "srcUriTemplate": "//images.vrt.be/orig/2020/09/28/294a2c15-018f-11eb-aae0-02b7b76bf47f.jpg",
        "focalPoint": "50% 50%",
        "id": "image",
        "hiddenInApp": false,
        ":type": "vrtvideo/components/content/image"
      },
      "description": "Kijk gratis online naar je favoriete programma's over wetenschap en natuur met VRT NU via de site of app.",
      "reference": {
        "link": "/vrtnu/categorieen/wetenschap-en-natuur.html",
        "modelUri": "/vrtnu/categorieen/wetenschap-en-natuur.model.json",
        "permalink": "https://vrtnu.page.link/UrW4TZWcy2sYN1Tj9",
        "referenceType": "internal"
      },
      "actions": [],
      ":type": "vrtvideo/components/structure/page-category"
    }
  ],
  "showTotal": false,
  "orderBy": "jcr:title",
  "isDescending": false,
  "sliderCentered": false,
  "listProviderType": "children",
  "tagsMatch": "any",
  "parentPage": "/content/vrtvideo/nl/categorieen",
  "carouselAutoplayDisabled": false,
  "template": "default",
  "pages": [],
  "empty": false,
  "size": 19,
  "tags": [],
  "limit": 0,
  ":type": "vrtvideo/components/video/normallist"
}
    """
    }
}

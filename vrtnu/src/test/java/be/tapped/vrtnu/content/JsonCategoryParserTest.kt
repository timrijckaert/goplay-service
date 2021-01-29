package be.tapped.vrtnu.content

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

public class JsonCategoryParserTest : StringSpec() {

    private val jsonCategoryParser = JsonCategoryParser(
        CategorySanitizer(
            UrlPrefixMapper(), ImageSanitizer(UrlPrefixMapper())
        )
    )

    init {
        "should be able to parse" {
            val categoriesJson = javaClass.classLoader?.getResourceAsStream("categories.json")!!.reader().readText()
            val categories = jsonCategoryParser.parse(categoriesJson).orNull()!!
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

            categories.map(Category::imageStoreUrl) shouldBe listOf(
                "https://images.vrt.be/orig/2016/10/03/de141920-8965-11e6-aef1-00163edf48dd.jpg",
                "https://images.vrt.be/orig/2020/11/16/8902ae62-2824-11eb-aae0-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2020/11/18/bcafcbc9-29bc-11eb-aae0-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2020/11/07/42168d27-214b-11eb-aae0-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2019/10/10/857c5c0e-eb3e-11e9-abcc-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2020/11/03/e85c8723-1e10-11eb-aae0-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2020/09/02/51fc23a0-ed1b-11ea-aae0-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2020/09/02/86d13982-ed1b-11ea-aae0-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2019/08/22/4c313980-c4c0-11e9-abcc-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2019/05/21/f68be9d1-7bcc-11e9-abcc-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2020/09/06/b24eb785-f07e-11ea-aae0-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2019/12/13/74be7c86-1d93-11ea-abcc-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2020/11/18/ddcf2cb5-29ba-11eb-aae0-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2016/04/29/f2c4e5f1-0de9-11e6-8682-00163edf843f.jpg",
                "https://images.vrt.be/orig/2018/12/28/990e4161-0ad9-11e9-abcc-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2020/01/02/a9ec46c4-2d7c-11ea-abcc-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2020/11/10/fbcb28b3-2360-11eb-aae0-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2016/10/03/d9f4f3ec-8965-11e6-aef1-00163edf48dd.jpg",
                "https://images.vrt.be/orig/2020/09/28/294a2c15-018f-11eb-aae0-02b7b76bf47f.jpg",
            )

            categories.map { it.image.src } shouldBe listOf(
                "https://images.vrt.be/orig/2016/10/03/de141920-8965-11e6-aef1-00163edf48dd.jpg",
                "https://images.vrt.be/orig/2020/11/16/8902ae62-2824-11eb-aae0-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2020/11/18/bcafcbc9-29bc-11eb-aae0-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2020/11/07/42168d27-214b-11eb-aae0-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2019/10/10/857c5c0e-eb3e-11e9-abcc-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2020/11/03/e85c8723-1e10-11eb-aae0-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2020/09/02/51fc23a0-ed1b-11ea-aae0-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2020/09/02/86d13982-ed1b-11ea-aae0-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2019/08/22/4c313980-c4c0-11e9-abcc-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2019/05/21/f68be9d1-7bcc-11e9-abcc-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2020/09/06/b24eb785-f07e-11ea-aae0-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2019/12/13/74be7c86-1d93-11ea-abcc-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2020/11/18/ddcf2cb5-29ba-11eb-aae0-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2016/04/29/f2c4e5f1-0de9-11e6-8682-00163edf843f.jpg",
                "https://images.vrt.be/orig/2018/12/28/990e4161-0ad9-11e9-abcc-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2020/01/02/a9ec46c4-2d7c-11ea-abcc-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2020/11/10/fbcb28b3-2360-11eb-aae0-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2016/10/03/d9f4f3ec-8965-11e6-aef1-00163edf48dd.jpg",
                "https://images.vrt.be/orig/2020/09/28/294a2c15-018f-11eb-aae0-02b7b76bf47f.jpg",
            )

            categories.map { it.image.srcUriTemplate } shouldBe listOf(
                "https://images.vrt.be/orig/2016/10/03/de141920-8965-11e6-aef1-00163edf48dd.jpg",
                "https://images.vrt.be/orig/2020/11/16/8902ae62-2824-11eb-aae0-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2020/11/18/bcafcbc9-29bc-11eb-aae0-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2020/11/07/42168d27-214b-11eb-aae0-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2019/10/10/857c5c0e-eb3e-11e9-abcc-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2020/11/03/e85c8723-1e10-11eb-aae0-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2020/09/02/51fc23a0-ed1b-11ea-aae0-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2020/09/02/86d13982-ed1b-11ea-aae0-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2019/08/22/4c313980-c4c0-11e9-abcc-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2019/05/21/f68be9d1-7bcc-11e9-abcc-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2020/09/06/b24eb785-f07e-11ea-aae0-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2019/12/13/74be7c86-1d93-11ea-abcc-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2020/11/18/ddcf2cb5-29ba-11eb-aae0-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2016/04/29/f2c4e5f1-0de9-11e6-8682-00163edf843f.jpg",
                "https://images.vrt.be/orig/2018/12/28/990e4161-0ad9-11e9-abcc-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2020/01/02/a9ec46c4-2d7c-11ea-abcc-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2020/11/10/fbcb28b3-2360-11eb-aae0-02b7b76bf47f.jpg",
                "https://images.vrt.be/orig/2016/10/03/d9f4f3ec-8965-11e6-aef1-00163edf48dd.jpg",
                "https://images.vrt.be/orig/2020/09/28/294a2c15-018f-11eb-aae0-02b7b76bf47f.jpg",
            )
        }
    }
}

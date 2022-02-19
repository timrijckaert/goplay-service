package be.tapped.goplay

import be.tapped.goplay.content.AllProgramsHtmlJsonExtractor
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldHaveSize

internal class HtmlJsonProgramExtractorTest : ShouldSpec({
    should("extract the programs from the HTML page") {
        val sut = AllProgramsHtmlJsonExtractor()
        val programs = sut.parse(PROGRAMS)
        programs.shouldBeRight().shouldHaveSize(2)
    }
})

private const val PROGRAMS =
    """<html>
<a data-program="{&quot;id&quot;:&quot;9a0484e7-ca02-4b19-9199-2fbe6c472c12&quot;,&quot;title&quot;:&quot;A Christmas Break&quot;,&quot;link&quot;:&quot;\/a-christmas-break&quot;,&quot;label&quot;:&quot;&quot;,&quot;needs16PlusLabel&quot;:false,&quot;category&quot;:&quot;Film&quot;,&quot;images&quot;:{&quot;poster&quot;:&quot;https:\/\/wmimages.goplay.be\/styles\/d03a19a83908ec0fa0ca867113b0553c8bce6d67\/2021-12\/onlinempl1050x1500a-christmas-break-r4l6cj.jpg?style=W3sicmVzaXplIjp7ImZpdCI6Imluc2lkZSIsIndpZHRoIjo0MDAsImhlaWdodCI6NTgwLCJ3aXRob3V0RW5sYXJnZW1lbnQiOmZhbHNlfX0seyJqcGVnIjp7InF1YWxpdHkiOjk1fX1d&amp;sign=0ef6680ce6a67a4622613be5231897bf7064c6a7f22355063979871952a89098&quot;,&quot;teaser&quot;:&quot;https:\/\/wmimages.goplay.be\/styles\/2a4e9f9d7142af06c50511f683da39ad08740d60\/meta\/onlinewallpaper3840x2160a-christmas-break-r4l6bg.jpg?style=W3sicmVzaXplIjp7ImZpdCI6ImNvdmVyIiwid2lkdGgiOjY3MCwiaGVpZ2h0IjozNzAsImdyYXZpdHkiOiJjZW50ZXIiLCJ3aXRob3V0RW5sYXJnZW1lbnQiOmZhbHNlfX0seyJqcGVnIjp7InF1YWxpdHkiOjg1fX1d&amp;sign=3519719cee197e9810037e46693823c1bbd9b8c336ade6be5ea50d90377f02d5&quot;},&quot;pageInfo&quot;:{&quot;brand&quot;:&quot;Play7&quot;}}"></a>
<a data-program="{&quot;id&quot;:&quot;9a0484e7-ca02-4b19-9199-2fbe6c472c12&quot;,&quot;title&quot;:&quot;A Christmas Break&quot;,&quot;link&quot;:&quot;\/a-christmas-break&quot;,&quot;label&quot;:&quot;&quot;,&quot;needs16PlusLabel&quot;:false,&quot;category&quot;:&quot;Film&quot;,&quot;images&quot;:{&quot;poster&quot;:&quot;https:\/\/wmimages.goplay.be\/styles\/d03a19a83908ec0fa0ca867113b0553c8bce6d67\/2021-12\/onlinempl1050x1500a-christmas-break-r4l6cj.jpg?style=W3sicmVzaXplIjp7ImZpdCI6Imluc2lkZSIsIndpZHRoIjo0MDAsImhlaWdodCI6NTgwLCJ3aXRob3V0RW5sYXJnZW1lbnQiOmZhbHNlfX0seyJqcGVnIjp7InF1YWxpdHkiOjk1fX1d&amp;sign=0ef6680ce6a67a4622613be5231897bf7064c6a7f22355063979871952a89098&quot;,&quot;teaser&quot;:&quot;https:\/\/wmimages.goplay.be\/styles\/2a4e9f9d7142af06c50511f683da39ad08740d60\/meta\/onlinewallpaper3840x2160a-christmas-break-r4l6bg.jpg?style=W3sicmVzaXplIjp7ImZpdCI6ImNvdmVyIiwid2lkdGgiOjY3MCwiaGVpZ2h0IjozNzAsImdyYXZpdHkiOiJjZW50ZXIiLCJ3aXRob3V0RW5sYXJnZW1lbnQiOmZhbHNlfX0seyJqcGVnIjp7InF1YWxpdHkiOjg1fX1d&amp;sign=3519719cee197e9810037e46693823c1bbd9b8c336ade6be5ea50d90377f02d5&quot;},&quot;pageInfo&quot;:{&quot;brand&quot;:&quot;Play7&quot;}}"></a>
</body>
</html>
    """

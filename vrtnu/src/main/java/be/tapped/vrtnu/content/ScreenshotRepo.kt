package be.tapped.vrtnu.content

public interface ScreenshotRepo {
    public enum class Brand {
        EEN,
        CANVAS,
        KETNET;
    }

    public fun screenshotForBrand(brand: Brand): String
}

public object DefaultScreenshotRepo : ScreenshotRepo {

    private const val BASE_SCREENSHOT_URL = "https://www.vrt.be/vrtnu-static/screenshots/"

    private fun convertBrandToUrlPath(brand: ScreenshotRepo.Brand): String =
        when (brand) {
            ScreenshotRepo.Brand.EEN -> "een"
            ScreenshotRepo.Brand.CANVAS -> "canvas"
            ScreenshotRepo.Brand.KETNET -> "ketnet"
        }

    override fun screenshotForBrand(brand: ScreenshotRepo.Brand): String = "$BASE_SCREENSHOT_URL/${convertBrandToUrlPath(brand)}.jpg"
}

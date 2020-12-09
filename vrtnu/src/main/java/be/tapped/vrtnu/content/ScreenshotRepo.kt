package be.tapped.vrtnu.content

public interface ScreenshotRepo {
    public fun screenshotForBrand(brandName: String): String
}

public object DefaultScreenshotRepo : ScreenshotRepo {
    private const val BASE_SCREENSHOT_URL = "https://vrtnu-api.vrt.be/screenshots"
    override fun screenshotForBrand(brandName: String): String = "$BASE_SCREENSHOT_URL/${brandName.toLowerCase()}.jpg"
}

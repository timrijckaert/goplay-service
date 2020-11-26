package be.tapped.vtmgo.content

import be.tapped.vtmgo.profile.VTMGOProduct
import okhttp3.HttpUrl

internal object BaseContentHttpUrlBuilder {
    fun constructBaseContentUrl(vtmGoProduct: VTMGOProduct): HttpUrl.Builder {
        val vtmGoProductToUrlPath = when (vtmGoProduct) {
            VTMGOProduct.VTM_GO -> "vtmgo"
            VTMGOProduct.VTM_GO_KIDS -> "vtmgo-kids"
        }
        return HttpUrl.Builder()
            .scheme("https")
            .host("lfvp-api.dpgmedia.net")
            .addPathSegments(vtmGoProductToUrlPath)
    }
}

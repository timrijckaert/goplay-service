package be.tapped.vtmgo.authentication

import okhttp3.Headers

class HeaderBuilder {
    companion object {
        private const val HEADER_X_DPP_JWT = "x-dpp-jwt"
    }

    // https://github.com/add-ons/plugin.video.vtm.go/wiki/API#vtm-go-api
    private val defaultHeaders =
        Headers.Builder()
            .add("x-app-version", "8")
            .add("x-persgroep-mobile-app", "true")
            .add("x-persgroep-os", "android")
            .add("x-persgroep-os-version", "23")
            .build()

    fun authenticationHeaders(jwt: JWT): Headers =
        Headers.Builder()
            .addAll(defaultHeaders)
            .add(HEADER_X_DPP_JWT, jwt.token)
            .build()
}

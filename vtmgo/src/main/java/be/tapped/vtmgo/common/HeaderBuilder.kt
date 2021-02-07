package be.tapped.vtmgo.common

import be.tapped.vtmgo.profile.JWT
import be.tapped.vtmgo.profile.Profile
import okhttp3.Headers

public sealed interface HeaderBuilder {

    public val defaultHeaders: Headers

    public fun authenticationHeaders(jwt: JWT, profile: Profile? = null): Headers
}

internal class AuthorizationHeaderBuilder : HeaderBuilder {

    // https://github.com/add-ons/plugin.video.vtm.go/wiki/API#vtm-go-api
    override val defaultHeaders =
        Headers.Builder().add("User-Agent", "VTMGO/6.11.18 (be.vmma.vtm.zenderapp; build:12648; Android 25) okhttp/4.7.2").add("x-app-version", "8")
            .add("x-persgroep-mobile-app", "true").add("x-persgroep-os", "android").add("x-persgroep-os-version", "25").build()

    override fun authenticationHeaders(jwt: JWT, profile: Profile?): Headers =
        Headers.Builder().addAll(defaultHeaders).add("x-dpp-jwt", jwt.token).apply {
            profile?.let {
                add("x-dpp-profile", it.id)
            }
        }.build()
}

package be.tapped.vtmgo.common

import be.tapped.common.internal.DefaultCookieJar
import be.tapped.vtmgo.profile.CookieJar

internal val defaultCookieJar = CookieJar(DefaultCookieJar())

package be.tapped.common.internal

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType

@InterModuleUseOnly
public val jsonMediaType: MediaType = "application/json; charset=utf-8".toMediaType()

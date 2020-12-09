package be.tapped.common.internal

import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@InterModuleUseOnly
public suspend fun OkHttpClient.executeAsync(request: Request): Response =
    suspendCancellableCoroutine { continuation ->
        newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                continuation.resumeWithException(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (continuation.isCancelled) return
                continuation.resume(response)
            }
        })
    }

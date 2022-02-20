package be.tapped.goplay.desktop

import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import arrow.core.Either
import be.tapped.goplay.GoPlayApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Image.Companion.makeFromEncoded
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.imageio.ImageIO

public suspend fun main(): Unit {
    application {
        val programs by suspend { GoPlayApi.fetchPrograms() }.asFlow().collectAsState(null)

        Window(
            onCloseRequest = ::exitApplication,
            title = "GoPlay",
            state = rememberWindowState(width = 600.dp, height = 500.dp)
        ) {
            Box(Modifier.fillMaxSize()) {
                val lazyListState: LazyListState = rememberLazyListState()
                when (val p = programs) {
                    null -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                    is Either.Left -> Text(p.value.toString())
                    is Either.Right -> {
                        LazyColumn(Modifier.fillMaxSize(), state = lazyListState) {
                            items(p.value.programs) {
                                Row(Modifier.fillMaxWidth()) {
                                    val imageUrl = it.images.poster

                                    fetchImage(imageUrl)?.let {
                                        Image(
                                            it,
                                            contentDescription = "personName",
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }

                                    Text(
                                        it.title,
                                        Modifier.clickable {

                                        }
                                    )
                                }
                            }
                        }
                        VerticalScrollbar(
                            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                            adapter = rememberScrollbarAdapter(scrollState = lazyListState)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun fetchImage(url: String): ImageBitmap? {
    var image by remember(url) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(url) {
        loadFullImage(url)?.let {
            image = makeFromEncoded(toByteArray(it)).asImageBitmap()
        }
    }

    return image
}

private fun toByteArray(bitmap: BufferedImage): ByteArray {
    val baos = ByteArrayOutputStream()
    ImageIO.write(bitmap, "png", baos)
    return baos.toByteArray()
}

private suspend fun loadFullImage(source: String): BufferedImage? =
    withContext(Dispatchers.IO) {
        runCatching {
            val url = URL(source)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 5000
            connection.connect()

            val input: InputStream = connection.inputStream
            val bitmap: BufferedImage? = ImageIO.read(input)
            bitmap
        }.getOrNull()
    }

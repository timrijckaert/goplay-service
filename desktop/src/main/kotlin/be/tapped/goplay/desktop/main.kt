package be.tapped.goplay.desktop

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import arrow.core.Either
import be.tapped.goplay.GoPlayApi
import io.kamel.core.config.DefaultCacheSize
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.fileFetcher
import io.kamel.core.config.httpFetcher
import io.kamel.core.config.stringMapper
import io.kamel.core.config.takeFrom
import io.kamel.core.config.uriMapper
import io.kamel.core.config.urlMapper
import io.kamel.image.KamelImage
import io.kamel.image.config.Default
import io.kamel.image.config.LocalKamelConfig
import io.kamel.image.config.imageBitmapDecoder
import io.kamel.image.config.resourcesFetcher
import io.kamel.image.lazyPainterResource
import kotlinx.coroutines.flow.asFlow

@OptIn(ExperimentalMaterialApi::class)
public suspend fun main(): Unit {
    application {
        val programs by suspend { GoPlayApi.fetchPrograms() }.asFlow().collectAsState(null)
        val desktopConfig =
            KamelConfig {
                imageBitmapCacheSize = 500
                imageVectorCacheSize = 500
                imageBitmapDecoder()
                stringMapper()
                urlMapper()
                uriMapper()
                fileFetcher()
                httpFetcher()
            }

        CompositionLocalProvider(LocalKamelConfig provides desktopConfig) {
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
                                    Row(Modifier.fillMaxWidth().clickable {}) {
                                        // val imageUrl = it.images.poster
                                        // KamelImage(
                                        //     modifier = Modifier.width(40.dp),
                                        //     crossfade = true,
                                        //     onLoading = {
                                        //         Box(modifier = Modifier.fillMaxSize()) {
                                        //             CircularProgressIndicator()
                                        //         }
                                        //     },
                                        //     resource = lazyPainterResource(imageUrl),
                                        //     contentDescription = it.title
                                        // )

                                        ListItem(
                                            icon = {
                                                // val width = 40.dp
                                                // val imageUrl = it.images.poster
                                                // KamelImage(
                                                //     modifier = Modifier.width(width),
                                                //     crossfade = true,
                                                //     onLoading = {
                                                //         Box(modifier = Modifier.width(width)) {
                                                //             CircularProgressIndicator()
                                                //         }
                                                //     },
                                                //     resource = lazyPainterResource(imageUrl),
                                                //     contentDescription = it.title
                                                // )
                                            },
                                            text = { Text(it.title) },
                                            secondaryText = { Text(it.label) },
                                            overlineText = { Text(it.pageInfo.brand.toString()) }
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
}

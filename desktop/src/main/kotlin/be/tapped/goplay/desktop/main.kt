package be.tapped.goplay.desktop

import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

public suspend fun main(): Unit = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "GoPlay",
        state = rememberWindowState(width = 600.dp, height = 500.dp)
    ) {

    }
}

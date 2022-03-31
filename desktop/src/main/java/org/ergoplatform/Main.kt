package org.ergoplatform

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.extensions.compose.jetbrains.lifecycle.LifecycleController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.badlogic.gdx.utils.I18NBundle
import com.badlogic.gdx.utils.ResourceWrapper
import org.ergoplatform.desktop.ui.DecomposeDesktopExampleTheme
import org.ergoplatform.desktop.ui.navigation.NavHostComponent
import org.ergoplatform.uilogic.STRING_APP_NAME

@ExperimentalAnimationApi
@ExperimentalMaterialApi
fun main() {
    val lifecycle = LifecycleRegistry()
    val root = NavHostComponent(DefaultComponentContext(lifecycle = lifecycle))
    application {

        val windowState = rememberWindowState()
        LifecycleController(lifecycle, windowState)

        Application.texts = I18NBundle.createBundle(ResourceWrapper("/i18n/strings"))

        Window(
            onCloseRequest = ::exitApplication,
            state = windowState,
            title = Application.texts.get(STRING_APP_NAME)
        ) {
            DecomposeDesktopExampleTheme {
                var show by remember { mutableStateOf(true) }

                root.render()
                AnimatedVisibility(visible = show)
                {
                    AlertDialog(onDismissRequest = {},
                        title = {
                            Text("A title")
                        },
                        text = {
                            Button(onClick = {}) {
                                Text("A button")
                            }
                            Text("A text")
                        },
                        confirmButton = {
                            Button(onClick = { show = false }) {
                                Text("A button")
                            }
                        })
                }
            }
        }
    }

}

object Application {
    lateinit var texts: I18NBundle
}
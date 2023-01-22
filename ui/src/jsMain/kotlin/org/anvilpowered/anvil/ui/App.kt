package org.anvilpowered.anvil.ui

import js.core.jso
import mui.material.CssBaseline
import mui.material.PaletteMode
import mui.material.styles.ThemeProvider
import mui.material.styles.createTheme
import react.FC
import react.Props
import react.create
import react.dom.client.createRoot
import web.dom.document
import web.html.HTML.div

fun main() {
    val root = document.createElement(div)
        .also { document.body.appendChild(it) }

    createRoot(root)
        .render(App.create())
}

private val darkTheme = createTheme(
    jso {
        palette = jso {
            mode = PaletteMode.dark
        }
    }
)

private val App = FC<Props> {
    ThemeProvider {
        theme = darkTheme
        CssBaseline ()
        Menu()
        Dashboard()
    }
}

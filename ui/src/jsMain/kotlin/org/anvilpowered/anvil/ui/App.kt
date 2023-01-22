package org.anvilpowered.anvil.ui

import csstype.em
import csstype.number
import csstype.px
import js.core.jso
import mui.material.Box
import mui.material.CssBaseline
import mui.material.PaletteMode
import mui.material.Toolbar
import mui.material.styles.ThemeProvider
import mui.material.styles.createTheme
import mui.system.sx
import react.FC
import react.Props
import react.create
import react.dom.client.createRoot
import react.router.dom.HashRouter
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
    // TODO: Convert to BrowserRouter and figure out how to fix webpack for SPA
    HashRouter {
        ThemeProvider {
            theme = darkTheme
            CssBaseline()
            Menu()
            Box {
                sx {
                    flexGrow = number(1.0)
                    padding = 2.em
                    marginLeft = drawerWidth.px
                }
                Toolbar()
                Routing()
            }
        }
    }
}

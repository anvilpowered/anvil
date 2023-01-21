package org.anvilpowered.anvil.ui

import csstype.Color
import csstype.number
import csstype.px
import emotion.css.css
import emotion.react.css
import js.core.jso
import mui.icons.material.Html
import mui.icons.material.Menu as MenuIcon
import mui.material.AppBar
import mui.material.AppBarPosition
import mui.material.Box
import mui.material.Button
import mui.material.ButtonColor
import mui.material.Divider
import mui.material.IconButton
import mui.material.IconButtonColor
import mui.material.IconButtonEdge
import mui.material.Size
import mui.material.Toolbar
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.sx
import react.ElementType
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

private val App = FC<Props> {
    Box {
        AppBar {
            position = AppBarPosition.static
            Toolbar {
                IconButton {
                    size = Size.large
                    edge = IconButtonEdge.start
                    color = IconButtonColor.inherit
                    sx {
                        marginRight = 2.px
                    }
                    child(MenuIcon.create())
                }
                Typography {
                    variant = TypographyVariant.h6
                    component = Divider
                    sx {
                        flexGrow = number(1.0)
                    }
                    +"Anvil"
                }
                Button {
                    color = ButtonColor.inherit
                    +"Login"
                }
            }
        }
    }
}

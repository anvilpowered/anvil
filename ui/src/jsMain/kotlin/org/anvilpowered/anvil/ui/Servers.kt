package org.anvilpowered.anvil.ui

import csstype.number
import csstype.px
import mui.material.Box
import mui.material.Toolbar
import mui.material.Typography
import mui.system.sx
import react.FC
import react.Props

val Servers = FC<Props> {
    Box {
        sx {
            flexGrow = number(1.0)
            padding = 3.px
            marginLeft = drawerWidth.px
        }
        Toolbar()
        Typography {
            paragraph = true
            +"""
                Servers
                """.trimIndent()
            +"""
                More servers
                """.trimIndent()

        }
    }
}

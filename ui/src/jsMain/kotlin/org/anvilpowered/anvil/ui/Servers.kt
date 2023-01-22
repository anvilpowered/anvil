package org.anvilpowered.anvil.ui

import mui.material.Typography
import react.FC
import react.Props

val Servers = FC<Props> {
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

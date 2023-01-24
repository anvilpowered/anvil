package org.anvilpowered.anvil.ui.page.servers

import csstype.Display
import csstype.number
import csstype.px
import mui.icons.material.ArrowBack
import mui.material.IconButton
import mui.material.Toolbar
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.sx
import react.FC
import react.Props
import react.router.useNavigate
import kotlin.random.Random

val ServersCreate = FC<Props> {

    val nav = useNavigate()

    Toolbar {
        sx {
            display = Display.flex
            gap = 16.px
        }
        Typography {
            sx {
                flexGrow = number(1.0)
            }
            variant = TypographyVariant.overline
            +"Create Server"
        }
        IconButton {
            onClick = { nav("/servers") }
            ArrowBack()
        }
    }
}

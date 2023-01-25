package org.anvilpowered.anvil.ui.page.servers.create

import csstype.Display
import csstype.em
import csstype.number
import csstype.px
import mui.icons.material.ArrowBack
import mui.material.Box
import mui.material.Divider
import mui.material.IconButton
import mui.material.Toolbar
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.Container
import mui.system.sx
import org.reduxkotlin.Store
import org.reduxkotlin.createStore
import org.reduxkotlin.createThreadSafeStore
import react.FC
import react.Props
import react.router.useNavigate



val ServerCreate = FC<Props> {

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
    Divider()

    Container {
        maxWidth = "lg"
        Box {
            sx {
                margin = 2.em
            }
        }
        ServerCreateStep1()
        createStore.subscribe {
            createStore.state.connectionString?.let {
                ServerCreateStep2 {
                    connectionString = it
                }
            }
        }
    }
}

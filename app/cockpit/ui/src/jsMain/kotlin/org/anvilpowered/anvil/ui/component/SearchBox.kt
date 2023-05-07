package org.anvilpowered.anvil.ui.component

import csstype.AlignItems
import csstype.Display
import csstype.LineStyle
import csstype.NamedColor
import csstype.ch
import csstype.px
import mui.material.InputBase
import mui.material.Paper
import mui.material.styles.Theme
import mui.material.styles.create
import mui.material.styles.useTheme
import mui.system.PropsWithSx
import mui.system.sx
import react.FC
import mui.icons.material.Search as SearchIcon

val SearchBox = FC<PropsWithSx> {

    val theme = useTheme<Theme>()

    Paper {
        sx {
            display = Display.flex
            alignItems = AlignItems.center
            padding = 3.px
            borderColor = NamedColor.gray
            borderWidth = 1.px
            borderStyle = LineStyle.solid
            borderRadius = 8.px
        }
        SearchIcon()
        InputBase {
            sx {
                width = 20.ch
                transition = theme.transitions.create("width") {
                    easing = theme.transitions.easing.easeIn
                    duration = theme.transitions.duration.shortest
                }
                focusWithin {
                    width = 34.ch
                }
            }
            placeholder = "Search..."
        }
    }
}

package org.anvilpowered.anvil.ui.page.servers

import csstype.AlignItems
import csstype.Display
import csstype.em
import csstype.number
import csstype.px
import mui.icons.material.ArrowBack
import mui.material.Box
import mui.material.CircularProgress
import mui.material.Container
import mui.material.Divider
import mui.material.IconButton
import mui.material.SvgIconColor
import mui.material.TextField
import mui.material.Toolbar
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.sx
import react.FC
import react.Props
import react.ReactNode
import react.dom.onChange
import react.router.useNavigate
import react.useState
import mui.icons.material.Check as CheckIcon

val ServersCreate = FC<Props> {

    val nav = useNavigate()
    var isValidName by useState<Boolean>()

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
            Typography {
                variant = TypographyVariant.h5
                +"Basic Information"
            }
            Divider {
                sx {
                    marginTop = 1.em
                    marginBottom = 2.em
                }
            }
            Box {
                sx {
                    display = Display.inlineFlex
                    alignItems = AlignItems.center
                    gap = 1.em
                }
                TextField {
                    id = "server-name1"
                    label = ReactNode("Server Name")
                    onChange = {
                        isValidName = it.target.asDynamic().value.length > 0
                    }
                }
                if (isValidName == true) {
                    CheckIcon {
                        color = SvgIconColor.success
                    }
                } else {
                    CircularProgress()
                }
            }
        }
    }
}

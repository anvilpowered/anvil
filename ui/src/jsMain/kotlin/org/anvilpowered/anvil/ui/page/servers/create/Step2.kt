package org.anvilpowered.anvil.ui.page.servers.create

import csstype.Auto
import csstype.Display
import csstype.JustifyContent
import csstype.em
import csstype.pct
import csstype.px
import js.core.jso
import mui.material.Alert
import mui.material.AlertColor
import mui.material.Box
import mui.material.Button
import mui.material.ButtonVariant
import mui.material.Card
import mui.material.CardActions
import mui.material.CardContent
import mui.material.Divider
import mui.material.Snackbar
import mui.material.SnackbarOriginHorizontal
import mui.material.SnackbarOriginVertical
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.sx
import react.FC
import react.Props
import react.useState
import web.navigator.navigator

external interface ServerCreateStep2Props : Props {
    var connectionString: String
}

val ServerCreateStep2 = FC<ServerCreateStep2Props> { step2Props ->
    var copySnackOpen by useState(false)
    Typography {
        variant = TypographyVariant.h5
        +"Step 2: Copy Connection String"
    }
    Divider {
        sx {
            marginTop = 1.em
            marginBottom = 2.em
        }
    }

    Card {
        sx {
            marginTop = 2.em
            width = 100.pct
        }
        CardContent {
            sx {
                height = 120.px
            }
            Typography {
                variant = TypographyVariant.overline
                +"Connection String"
            }
            Box {
                sx {
                    display = Display.flex
                    justifyContent = JustifyContent.center
                }
                Typography {
                    variant = TypographyVariant.h5
                    +step2Props.connectionString
                }
            }
        }
        CardActions {
            Button {
                sx {
                    marginLeft = Auto.auto
                }
                variant = ButtonVariant.outlined
                onClick = {
                    navigator.clipboard.writeText(step2Props.connectionString)
                    copySnackOpen = true
                }
                +"Copy"
            }
        }
    }
    Snackbar {
        anchorOrigin = jso {
            vertical = SnackbarOriginVertical.bottom
            horizontal = SnackbarOriginHorizontal.center
        }
        open = copySnackOpen
        autoHideDuration = 2000
        onClose = { _, _ -> copySnackOpen = false }
        Alert {
            severity = AlertColor.success
            +"Copied to clipboard!"
        }
    }
}

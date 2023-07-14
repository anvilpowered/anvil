package org.anvilpowered.anvil.ui.page.servers

import csstype.AlignItems
import csstype.Auto
import csstype.Display
import csstype.JustifyContent
import csstype.em
import csstype.number
import csstype.pct
import csstype.px
import js.core.jso
import mui.icons.material.ArrowBack
import mui.material.Alert
import mui.material.AlertColor
import mui.material.Box
import mui.material.Button
import mui.material.ButtonVariant
import mui.material.Card
import mui.material.CardActions
import mui.material.CardContent
import mui.material.CircularProgress
import mui.material.Container
import mui.material.Divider
import mui.material.IconButton
import mui.material.Snackbar
import mui.material.SnackbarOriginHorizontal
import mui.material.SnackbarOriginVertical
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
import web.navigator.navigator
import kotlin.random.Random
import kotlin.random.nextUInt
import mui.icons.material.Check as CheckIcon
import mui.icons.material.Close as CloseIcon

val ServersCreate = FC<Props> {

    val nav = useNavigate()
    var isValidName by useState<Boolean>()
    var isNameDirty by useState(false)
    var generatedConnectionString by useState<String>()
    var copySnackOpen by useState(false)
    val random by useState(Random(1))

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
                +"Step 1: Server Information"
            }
            Divider {
                sx {
                    marginTop = 1.em
                    marginBottom = 2.em
                }
            }
            Box {
                sx {
                    display = Display.flex
                    alignItems = AlignItems.center
                    gap = 1.em
                    marginBottom = 2.em
                }
                TextField {
                    id = "server-name"
                    label = ReactNode("Server Name")
                    onChange = {
                        isValidName = it.target.asDynamic().value.length > 0
                    }
                }
                when {
                    isValidName == true -> CheckIcon { color = SvgIconColor.success }
                    isNameDirty && isValidName == false -> CloseIcon()
                    isNameDirty && isValidName == null -> CircularProgress()
                }
                Box {
                    sx {
                        flexGrow = number(1.0)
                    }
                }
                Button {
                    variant = ButtonVariant.outlined
                    disabled = isValidName != true
                    onClick = {
                        generatedConnectionString = random.nextUInt().toString()
                    }
                    if (generatedConnectionString == null) {
                        +"Generate Connection String"
                    } else {
                        +"Regenerate Connection String"
                    }
                }
            }

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
                    generatedConnectionString?.let {
                        Box {
                            sx {
                                display = Display.flex
                                justifyContent = JustifyContent.center
                            }
                            Typography {
                                variant = TypographyVariant.h5
                                +it
                            }
                        }
                    }
                }
                CardActions {
                    Button {
                        sx {
                            marginLeft = Auto.auto
                        }
                        variant = ButtonVariant.outlined
                        disabled = generatedConnectionString == null
                        generatedConnectionString?.let { cs ->
                            onClick = {
                                navigator.clipboard.writeText(cs)
                                copySnackOpen = true
                            }
                        }
                        +"Copy"
                    }
                }
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

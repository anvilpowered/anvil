package org.anvilpowered.anvil.ui.page.servers.create

import csstype.AlignItems
import csstype.Display
import csstype.em
import csstype.number
import mui.icons.material.Check
import mui.icons.material.Close
import mui.material.Box
import mui.material.Button
import mui.material.ButtonVariant
import mui.material.CircularProgress
import mui.material.Divider
import mui.material.SvgIconColor
import mui.material.TextField
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.sx
import react.FC
import react.Props
import react.ReactNode
import react.dom.onChange
import react.useState
import kotlin.random.Random
import kotlin.random.nextUInt

val ServerCreateStep1 = FC<Props> {

    var isValidName by useState<Boolean>()
    var isNameDirty by useState(false)
    val random by useState(Random(1))

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
            isValidName == true -> Check { color = SvgIconColor.success }
            isNameDirty && isValidName == false -> Close()
            isNameDirty && isValidName == null -> CircularProgress()
        }
        Box {
            sx {
                flexGrow = number(1.0)
            }
        }
//        Button {
//            variant = ButtonVariant.outlined
//            disabled = isValidName != true
//            onClick = {
//                createStore.dispatch(GenerateConnectionStringAction(random.nextUInt().toString()))
//            }
//            if (createStore.state.connectionString == null) {
//                +"Generate Connection String"
//            } else {
//                +"Regenerate Connection String"
//            }
//        }
    }
}

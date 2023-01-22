package org.anvilpowered.anvil.ui.component

import csstype.em
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.sx
import react.FC
import react.Props

external interface SectionTypographyProps : Props {
    var text: String
}

val SectionTypography = FC<SectionTypographyProps> {
    Typography {
        sx { paddingLeft = 1.em }
        variant = TypographyVariant.overline
        noWrap = true
        +it.text
    }
}

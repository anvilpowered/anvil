package org.anvilpowered.anvil.ui

import csstype.BoxSizing
import csstype.Display
import csstype.em
import csstype.minus
import csstype.pct
import csstype.px
import mui.icons.material.ViewSidebar
import mui.material.AppBar
import mui.material.AppBarPosition
import mui.material.Box
import mui.material.CssBaseline
import mui.material.Divider
import mui.material.Drawer
import mui.material.DrawerAnchor
import mui.material.DrawerVariant
import mui.material.ListItemIcon
import mui.material.ListItemText
import mui.material.MenuItem
import mui.material.MenuList
import mui.material.Toolbar
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.sx
import org.anvilpowered.anvil.ui.component.SectionTypography
import react.FC
import react.Props
import mui.icons.material.Inbox as InboxIcon

val drawerWidth = 240;

val Menu = FC<Props> {
    Box {
        sx { display = Display.flex }
        CssBaseline()
        AppBar {
            position = AppBarPosition.fixed
            sx {
                width = 100.pct - drawerWidth.px
                marginLeft = drawerWidth.px
            }
            Toolbar {
                Typography {
                    variant = TypographyVariant.h6
                    noWrap = true
                    component = Divider
                    +"Anvil Dashboard"
                }
            }
        }
        Drawer {
            sx {
                width = drawerWidth.px
                "& .MuiDrawer-paper" {
                    width = drawerWidth.px
                    boxSizing = BoxSizing.borderBox
                }
            }
            variant = DrawerVariant.permanent
            anchor = DrawerAnchor.left
            Toolbar()
            SectionTypography {
                text = "Foobar"
            }
            MenuList {
                sequenceOf("Foobar", "Baz", "Qux").forEach {
                    MenuItem {
                        ListItemIcon {
                            ViewSidebar()
                        }
                        ListItemText {
                            +"Hello $it"
                        }
                    }
                }
            }
        }
    }
}

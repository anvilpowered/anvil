package org.anvilpowered.anvil.ui.page.servers

import csstype.Display
import csstype.number
import csstype.px
import mui.material.IconButton
import mui.material.Paper
import mui.material.Table
import mui.material.TableBody
import mui.material.TableCell
import mui.material.TableCellAlign
import mui.material.TableContainer
import mui.material.TableHead
import mui.material.TableRow
import mui.material.Toolbar
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.sx
import org.anvilpowered.anvil.ui.component.SearchBox
import org.anvilpowered.anvil.user.Server
import react.FC
import react.Props
import react.router.useNavigate
import mui.icons.material.Add as AddIcon

private data class ServerImpl(
    override val id: String,
    override val playerCount: Int,
    override val maxPlayerCount: Int,
) : Server

val demoData = listOf<Server>(
    ServerImpl("Spawn", 5, 150),
    ServerImpl("Wild 1", 17, 100),
    ServerImpl("Wild 2", 23, 100),
    ServerImpl("Wild 3", 39, 100),
    ServerImpl("Wild 4", 24, 100),
)

val ServersHome = FC<Props> {

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
            +"Servers"
        }
        SearchBox()
        IconButton {
            onClick = { nav("/servers/create") }
            AddIcon()
        }
    }
    TableContainer {
        component = Paper
        Table {
            TableHead {
                TableRow {
                    TableCell { +"Server Name" }
                    TableCell { align = TableCellAlign.right; +"Players" }
                }
            }
            TableBody {
                demoData.forEach { server ->
                    TableRow {
                        TableCell { +server.id }
                        TableCell { align = TableCellAlign.right; +"${server.playerCount}/${server.maxPlayerCount}" }
                    }
                }
            }
        }
    }
}

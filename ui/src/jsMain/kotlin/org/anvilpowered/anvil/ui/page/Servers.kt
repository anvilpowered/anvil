package org.anvilpowered.anvil.ui.page

import mui.material.Paper
import mui.material.Table
import mui.material.TableBody
import mui.material.TableCell
import mui.material.TableContainer
import mui.material.TableHead
import mui.material.TableRow
import org.anvilpowered.anvil.user.Server
import react.FC
import react.Props

private data class ServerImpl(
    override val id: String,
    override val playerCount: Int,
    override val maxPlayerCount: Int
) : Server

val demoData = listOf<Server>(
    ServerImpl("Spawn", 5, 150),
    ServerImpl("Wild 1", 17, 100),
    ServerImpl("Wild 2", 23, 100),
    ServerImpl("Wild 3", 39, 100),
    ServerImpl("Wild 4", 24, 100),
)

val Servers = FC<Props> {
    TableContainer {
        component = Paper
        Table {
            TableHead {
                TableRow {
                    TableCell { +"Server Name" }
                    TableCell { +"Players Online" }
                    TableCell { +"Player Capacity" }
                }
            }
            TableBody {
                demoData.forEach { server ->
                    TableRow {
                        TableCell { +server.id }
                        TableCell { +server.playerCount.toString() }
                        TableCell { +server.maxPlayerCount.toString() }
                    }
                }
            }
        }
    }
}

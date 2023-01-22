package org.anvilpowered.anvil.ui.page

import mui.material.Grid
import mui.material.Typography
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
    Grid
}

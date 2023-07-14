package org.anvilpowered.anvil.domain.system

interface Server {
    val id: String
    val playerCount: Int
    val maxPlayerCount: Int
}

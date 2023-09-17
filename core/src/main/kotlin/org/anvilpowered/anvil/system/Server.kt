package org.anvilpowered.anvil.system

interface Server {
    val id: String
    val playerCount: Int
    val maxPlayerCount: Int
}

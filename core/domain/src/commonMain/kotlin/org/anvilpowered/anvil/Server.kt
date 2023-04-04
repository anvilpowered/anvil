package org.anvilpowered.anvil

interface Server {
    val id: String
    val playerCount: Int
    val maxPlayerCount: Int
}

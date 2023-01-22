package org.anvilpowered.anvil.user

interface Server {
    val id: String
    val playerCount: Int
    val maxPlayerCount: Int
}

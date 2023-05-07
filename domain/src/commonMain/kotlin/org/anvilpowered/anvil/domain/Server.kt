package org.anvilpowered.anvil.domain

interface Server {
    val id: String
    val playerCount: Int
    val maxPlayerCount: Int
}

package org.anvilpowered.anvil.domain.user

interface Player : CommandSource {
    val user: GameUser
}

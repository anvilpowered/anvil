package org.anvilpowered.anvil.domain.user

interface Player : Audience, PermissionSubject {
    val user: GameUser
}

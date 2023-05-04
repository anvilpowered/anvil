package org.anvilpowered.anvil.agent.user

import org.anvilpowered.anvil.user.Audience
import org.anvilpowered.anvil.user.GameUser
import org.anvilpowered.anvil.user.PermissionSubject
import org.anvilpowered.anvil.user.Player
import com.velocitypowered.api.permission.PermissionSubject as VelocityPermissionSubject
import com.velocitypowered.api.proxy.Player as VelocityPlayer

fun VelocityPlayer.toAnvil(): Player = AnvilVelocityPlayer(this)

private class AnvilVelocityPlayer(
    val velocityPlayer: VelocityPlayer,
) : Player,
    Audience by velocityPlayer,
    PermissionSubject by (velocityPlayer as VelocityPermissionSubject).toAnvil() {

    override val user: GameUser
        get() = TODO("Not yet implemented")
}

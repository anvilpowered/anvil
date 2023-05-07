package org.anvilpowered.anvil.user

import org.anvilpowered.anvil.domain.user.Audience
import org.anvilpowered.anvil.domain.user.GameUser
import org.anvilpowered.anvil.domain.user.PermissionSubject
import org.anvilpowered.anvil.domain.user.Player
import kotlin.math.pow
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


fun foo(op1: Float, op2: Float) {

    '1'.code - '0'.code

}

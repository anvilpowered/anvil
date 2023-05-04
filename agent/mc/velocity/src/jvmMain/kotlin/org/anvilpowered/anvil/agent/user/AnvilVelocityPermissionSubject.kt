package org.anvilpowered.anvil.agent.user

import com.velocitypowered.api.permission.Tristate
import org.anvilpowered.anvil.user.PermissionSubject
import com.velocitypowered.api.permission.PermissionSubject as VelocityPermissionSubject

fun VelocityPermissionSubject.toAnvil(): PermissionSubject = AnvilVelocityPermissionSubject(this)

private class AnvilVelocityPermissionSubject(
    private val velocityPermissionSubject: VelocityPermissionSubject,
) : PermissionSubject {
    override fun hasPermission(permission: String): Boolean? =
        velocityPermissionSubject.getPermissionValue(permission).toBoolean()
}

private fun Tristate.toBoolean(): Boolean? {
    return when (this) {
        Tristate.TRUE -> true
        Tristate.FALSE -> false
        Tristate.UNDEFINED -> null
    }
}

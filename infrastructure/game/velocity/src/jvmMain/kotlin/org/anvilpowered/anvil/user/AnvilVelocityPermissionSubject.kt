package org.anvilpowered.anvil.user

import com.velocitypowered.api.permission.Tristate
import org.anvilpowered.anvil.domain.user.PermissionSubject
import com.velocitypowered.api.permission.PermissionSubject as VelocityPermissionSubject

fun VelocityPermissionSubject.toAnvil(): PermissionSubject = AnvilVelocityPermissionSubject(this)

private class AnvilVelocityPermissionSubject(
    private val velocityPermissionSubject: VelocityPermissionSubject,
) : PermissionSubject {
    override fun hasPermission(permission: String): Boolean? =
        velocityPermissionSubject.getPermissionValue(permission).toBoolean()
}

private fun Tristate.toBoolean(): Boolean? = when (this) {
    Tristate.TRUE -> true
    Tristate.FALSE -> false
    Tristate.UNDEFINED -> null
}

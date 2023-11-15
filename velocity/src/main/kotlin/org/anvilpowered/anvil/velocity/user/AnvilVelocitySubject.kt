package org.anvilpowered.anvil.velocity.user

import com.velocitypowered.api.permission.Tristate
import org.anvilpowered.anvil.core.user.Subject
import com.velocitypowered.api.permission.PermissionSubject as VelocitySubject

fun VelocitySubject.toAnvilSubject(): Subject = AnvilVelocitySubject(this)

private class AnvilVelocitySubject(
    override val platformDelegate: VelocitySubject,
) : Subject {
    override fun hasPermission(permission: String): Boolean? =
        platformDelegate.getPermissionValue(permission).toBoolean()
}

private fun Tristate.toBoolean(): Boolean? = when (this) {
    Tristate.TRUE -> true
    Tristate.FALSE -> false
    Tristate.UNDEFINED -> null
}

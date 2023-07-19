package org.anvilpowered.anvil.user

import com.velocitypowered.api.permission.Tristate
import org.anvilpowered.anvil.domain.user.Subject
import com.velocitypowered.api.permission.PermissionSubject as VelocitySubject

fun VelocitySubject.toAnvilSubject(): Subject = AnvilVelocitySubject(this)

private class AnvilVelocitySubject(
    private val velocitySubject: VelocitySubject,
) : Subject {
    override fun hasPermission(permission: String): Boolean? =
        velocitySubject.getPermissionValue(permission).toBoolean()
}

private fun Tristate.toBoolean(): Boolean? = when (this) {
    Tristate.TRUE -> true
    Tristate.FALSE -> false
    Tristate.UNDEFINED -> null
}

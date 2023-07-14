package org.anvilpowered.anvil.user

import org.anvilpowered.anvil.domain.user.Subject
import org.spongepowered.api.util.Tristate
import org.spongepowered.api.service.permission.Subject as SpongeSubject

fun SpongeSubject.toAnvilSubject(): Subject = AnvilSpongeSubject(this)

private class AnvilSpongeSubject(
    private val spongeSubject: SpongeSubject,
) : Subject {
    override fun hasPermission(permission: String): Boolean? =
        spongeSubject.permissionValue(permission).toBoolean()
}

private fun Tristate.toBoolean(): Boolean? = when (this) {
    Tristate.TRUE -> true
    Tristate.FALSE -> false
    Tristate.UNDEFINED -> null
}

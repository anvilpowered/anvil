package org.anvilpowered.anvil.sponge.user

import org.anvilpowered.anvil.core.user.Subject
import org.spongepowered.api.util.Tristate
import org.spongepowered.api.service.permission.Subject as SpongeSubject

fun SpongeSubject.toAnvilSubject(): Subject = AnvilSpongeSubject(this)

private class AnvilSpongeSubject(
    override val platformDelegate: SpongeSubject,
) : Subject {
    override fun hasPermission(permission: String): Boolean? =
        platformDelegate.permissionValue(permission).toBoolean()
}

private fun Tristate.toBoolean(): Boolean? = when (this) {
    Tristate.TRUE -> true
    Tristate.FALSE -> false
    Tristate.UNDEFINED -> null
}

package org.anvilpowered.anvil.user

import org.anvilpowered.anvil.domain.user.Subject
import org.bukkit.permissions.Permissible

fun Permissible.toAnvilSubject(): Subject = AnvilPaperSubject(this)

private class AnvilPaperSubject(
    private val paperSubject: Permissible,
) : Subject {
    override fun hasPermission(permission: String): Boolean? =
        paperSubject.permissionValue(permission).toBoolean()
}

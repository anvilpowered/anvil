package org.anvilpowered.anvil.paper.user

import org.anvilpowered.anvil.core.user.Subject
import org.bukkit.permissions.Permissible

fun Permissible.toAnvilSubject(): Subject = AnvilPaperSubject(this)

private class AnvilPaperSubject(
    override val platformDelegate: Permissible,
) : Subject {
    override fun hasPermission(permission: String): Boolean? {
        return if (platformDelegate.hasPermission(permission)) {
            true
        } else if (platformDelegate.isPermissionSet(permission)) {
            false
        } else {
            null
        }
    }
}

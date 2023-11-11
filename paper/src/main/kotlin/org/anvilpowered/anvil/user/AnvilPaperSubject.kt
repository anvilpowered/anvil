package org.anvilpowered.anvil.user

import org.anvilpowered.anvil.core.user.Subject
import org.bukkit.permissions.Permissible

fun Permissible.toAnvilSubject(): Subject = AnvilPaperSubject(this)

private class AnvilPaperSubject(
    private val paperSubject: Permissible,
) : Subject {
    override fun hasPermission(permission: String): Boolean? {
        return if (paperSubject.hasPermission(permission)) {
            true
        } else if (paperSubject.isPermissionSet(permission)) {
            false
        } else {
            null
        }
    }
}

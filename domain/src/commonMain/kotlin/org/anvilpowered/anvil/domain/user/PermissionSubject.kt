package org.anvilpowered.anvil.domain.user

interface PermissionSubject {

    /**
     * Checks if the subject has the specified permission.
     *
     * - A value of `true` indicates that the subject has the permission explicitly set.
     * - A value of `false` indicates that the subject has the permission explicitly set to false.
     * - A value of `null` indicates that the subject does not have the permission explicitly set.
     */
    fun hasPermission(permission: String): Boolean?
}

fun PermissionSubject.hasPermissionSet(permission: String): Boolean = hasPermission(permission) == true
fun PermissionSubject.hasPermissionUnset(permission: String): Boolean = hasPermission(permission) == null
fun PermissionSubject.hasPermissionNotSet(permission: String): Boolean = hasPermission(permission) == false

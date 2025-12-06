/*
 *   Anvil - AnvilPowered.org
 *   Copyright (C) 2019-2026 Contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.core.user

import org.anvilpowered.anvil.core.PlatformType

interface Subject : PlatformType {

  /**
   * Checks if the subject has the specified permission.
   *
   * - A value of `true` indicates that the subject has the permission explicitly set.
   * - A value of `false` indicates that the subject has the permission explicitly set to false.
   * - A value of `null` indicates that the subject does not have the permission explicitly set.
   */
  fun hasPermission(permission: String): Boolean?
}

fun Subject.hasPermissionSet(permission: String): Boolean = hasPermission(permission) == true
fun Subject.hasPermissionUnset(permission: String): Boolean = hasPermission(permission) == null
fun Subject.hasPermissionNotSet(permission: String): Boolean = !hasPermissionSet(permission)

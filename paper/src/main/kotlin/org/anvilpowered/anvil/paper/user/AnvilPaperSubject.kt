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

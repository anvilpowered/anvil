/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020-2021
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.anvilpowered.anvil.velocity.util

import org.anvilpowered.anvil.api.util.KickService
import com.velocitypowered.api.proxy.ProxyServer
import java.util.UUID
import org.anvilpowered.anvil.common.util.CommonUserService
import java.util.stream.Collectors
import java.util.concurrent.CompletableFuture
import com.velocitypowered.api.permission.PermissionSubject
import org.anvilpowered.anvil.api.command.CommandExecuteService
import org.anvilpowered.anvil.api.util.PermissionService

class VelocityPermissionService : PermissionService {
  override fun hasPermission(subject: Any?, permission: String): Boolean {
    return (subject is PermissionSubject
      && subject.hasPermission(permission))
  }
}
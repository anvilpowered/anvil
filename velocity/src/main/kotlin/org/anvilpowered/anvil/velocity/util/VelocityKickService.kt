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

import com.google.inject.Inject
import org.anvilpowered.anvil.api.util.KickService
import com.velocitypowered.api.proxy.ProxyServer
import java.util.UUID
import org.anvilpowered.anvil.common.util.CommonUserService
import java.util.stream.Collectors
import java.util.concurrent.CompletableFuture
import com.velocitypowered.api.permission.PermissionSubject
import com.velocitypowered.api.proxy.Player
import net.kyori.adventure.text.Component
import org.anvilpowered.anvil.api.command.CommandExecuteService

class VelocityKickService : KickService {
  @Inject
  private val proxyServer: ProxyServer? = null
  private fun getReason(reason: Any): Component {
    return if (reason is Component) reason else Component.text(reason.toString())
  }

  override fun kick(userUUID: UUID, reason: Any) {
    proxyServer!!.getPlayer(userUUID).ifPresent { p: Player -> p.disconnect(getReason(reason)) }
  }

  override fun kick(userName: String, reason: Any) {
    proxyServer!!.getPlayer(userName).ifPresent { p: Player -> p.disconnect(getReason(reason)) }
  }

  override fun kick(userUUID: UUID) {
    kick(userUUID, "You have been kicked")
  }

  override fun kick(userName: String) {
    kick(userName, "You have been kicked")
  }
}

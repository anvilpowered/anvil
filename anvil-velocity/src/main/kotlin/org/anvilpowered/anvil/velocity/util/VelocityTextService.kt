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
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.ProxyServer
import java.util.UUID
import net.kyori.adventure.identity.Identified
import net.kyori.adventure.identity.Identity
import net.kyori.adventure.text.Component
import org.anvilpowered.anvil.common.util.CommonTextService

class VelocityTextService : CommonTextService<CommandSource>() {

  @Inject
  private lateinit var proxyServer: ProxyServer

  override fun send(text: Component, receiver: CommandSource) {
    receiver.sendMessage(Identity.nil(), text)
  }

  override fun send(text: Component, receiver: CommandSource, sourceUUID: UUID) {
    receiver.sendMessage(Identity.identity(sourceUUID), text)
  }

  override fun send(text: Component, receiver: CommandSource, source: Any) {
    if (source is Identified) {
      receiver.sendMessage(source, text)
    } else {
      send(text, receiver)
    }
  }

  override fun getConsole(): CommandSource = proxyServer.consoleCommandSource
}

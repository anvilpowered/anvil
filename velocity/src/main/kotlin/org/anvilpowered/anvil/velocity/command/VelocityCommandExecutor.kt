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

package org.anvilpowered.anvil.velocity.command

import com.velocitypowered.api.proxy.ProxyServer
import kotlinx.coroutines.future.await
import org.anvilpowered.anvil.core.command.CommandExecutor
import org.anvilpowered.anvil.core.command.CommandSource
import com.velocitypowered.api.command.CommandSource as VelocityCommandSource

class VelocityCommandExecutor(
  private val proxyServer: ProxyServer,
) : CommandExecutor {
  override suspend fun execute(
    source: CommandSource,
    command: String,
  ): Boolean =
    proxyServer.commandManager
      .executeAsync(
        source.platformDelegate as VelocityCommandSource,
        command,
      ).await()

  override suspend fun executeAsConsole(command: String): Boolean =
    proxyServer.commandManager
      .executeAsync(
        proxyServer.consoleCommandSource,
        command,
      ).await()
}

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

package org.anvilpowered.anvil.velocity.platform

import com.velocitypowered.api.proxy.ProxyServer
import net.kyori.adventure.audience.Audience
import org.anvilpowered.anvil.core.platform.Platform
import org.anvilpowered.anvil.core.platform.Server

class VelocityServer(
  proxyServer: ProxyServer,
) : Server {
  override val platform: Platform = VelocityPlatform(proxyServer)
  override val broadcastAudience: Audience = proxyServer
  override val systemSubject: Audience = proxyServer.consoleCommandSource
}

/*
 *   Anvil - AnvilPowered.org
 *   Copyright (C) 2019-2023 Contributors
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

package org.anvilpowered.anvil.agent

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.permission.Tristate
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.audience.ForwardingAudience
import org.anvilpowered.anvil.user.CommandSource
import org.slf4j.Logger
import com.velocitypowered.api.command.CommandSource as VelocityCommandSource

@Plugin(
    id = "anvil-agent",
    name = "Anvil Agent",
    version = "0.4.0-SNAPSHOT",
    authors = ["AnvilPowered"],
)
class AnvilVelocityPlugin @Inject constructor(
    private val proxyServer: ProxyServer,
    private val logger: Logger,
) {

    @Subscribe
    fun onProxyInit(event: ProxyInitializeEvent) {
        logger.info("Anvil Agent is running!")

//        val commandNode = AnvilCommand.createPlugins()
//            .mapSource<_, VelocityCommandSource> { BridgeSource(it) }
//            .toBrigadier()
//
//        proxyServer.commandManager.register(BrigadierCommand(commandNode))
    }
}

class BridgeSource(private val original: VelocityCommandSource) : ForwardingAudience.Single, CommandSource {
    override fun audience(): Audience = original
    override fun hasPermission(permission: String): Boolean? = original.getPermissionValue(permission).toBoolean()
}


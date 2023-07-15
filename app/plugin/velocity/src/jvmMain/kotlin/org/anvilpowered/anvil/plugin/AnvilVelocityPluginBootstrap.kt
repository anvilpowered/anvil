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

package org.anvilpowered.anvil.plugin

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import org.anvilpowered.anvil.api.AnvilApi
import org.anvilpowered.anvil.createVelocity
import org.slf4j.Logger

@Plugin(
    id = "anvil-agent",
    name = "Anvil Agent",
    version = "0.4.0-SNAPSHOT",
    authors = ["AnvilPowered"],
)
class AnvilVelocityPluginBootstrap @Inject constructor(
    private val logger: Logger,
    private val proxyServer: ProxyServer,
) {

    private val plugin = with(AnvilApi.createVelocity(logger, proxyServer)) {
        AnvilVelocityPlugin()
    }

    @Subscribe
    fun onProxyInit(event: ProxyInitializeEvent) {
        logger.info("Anvil Agent is running!")
        proxyServer.eventManager.register(this, plugin)
        plugin.registerCommands()
    }
}

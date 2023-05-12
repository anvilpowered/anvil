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

package org.anvilpowered.anvil

import com.velocitypowered.api.proxy.ProxyServer
import org.anvilpowered.anvil.api.ApiBindings
import org.anvilpowered.anvil.api.GameApiBindings
import org.anvilpowered.anvil.api.LoggingScope
import org.anvilpowered.anvil.command.CommonGameUserCommandScope
import org.anvilpowered.anvil.domain.command.GameUserCommandScope
import org.anvilpowered.anvil.domain.platform.GamePlatform
import org.anvilpowered.anvil.domain.platform.PluginManager
import org.anvilpowered.anvil.infrastructure.datastore.CommonGameUserScope
import org.anvilpowered.anvil.platform.VelocityGamePlatform
import org.anvilpowered.anvil.platform.VelocityPluginManager

fun ApiBindings.Companion.createVelocity(proxyServer: ProxyServer): GameApiBindings {
    val gameUserScope = CommonGameUserScope()

    class VelocityApiBindings(val proxyServer: ProxyServer) : GameApiBindings,
        GamePlatform by VelocityGamePlatform(proxyServer),
        LoggingScope by LoggingScope.create("anvil-velocity"),
        PluginManager.Scope by VelocityPluginManager.createScope(proxyServer.pluginManager),
        GameUserCommandScope by (with(gameUserScope) { CommonGameUserCommandScope() })

    return VelocityApiBindings(proxyServer)
}

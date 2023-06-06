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

package org.anvilpowered.anvil.platform

import org.anvilpowered.anvil.domain.platform.Plugin
import org.anvilpowered.anvil.domain.platform.PluginManager
import com.velocitypowered.api.plugin.PluginManager as BackingPluginManager

internal class VelocityPluginManager(private val backing: BackingPluginManager) : PluginManager {
    override val plugins: List<Plugin>
        get() = backing.plugins.map { VelocityPlugin(it) }

    companion object {
        fun createScope(backing: BackingPluginManager) = object : PluginManager.Scope {
            override val pluginManager: PluginManager = VelocityPluginManager(backing)
        }
    }
}
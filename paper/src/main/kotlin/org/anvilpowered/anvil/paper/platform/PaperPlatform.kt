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

@file:Suppress("UnstableApiUsage")

package org.anvilpowered.anvil.paper.platform

import org.anvilpowered.anvil.core.platform.Platform
import org.anvilpowered.anvil.core.platform.PluginMeta
import org.bukkit.Bukkit

internal object PaperPlatform : Platform {
  override val isProxy: Boolean = false
  override val plugins: List<PluginMeta>
    get() = Bukkit.getPluginManager().plugins.map { it.pluginMeta.toAnvilPluginMeta() }
  override val name: String
    get() = "paper"
  override val version: String
    get() = Bukkit.getBukkitVersion()
}

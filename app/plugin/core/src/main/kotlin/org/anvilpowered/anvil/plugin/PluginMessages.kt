/*
 *   Anvil - AnvilPowered.org
 *   Copyright (C) 2019-2024 Contributors
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

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

object PluginMessages {
    val pluginPrefix = Component.text("[Anvil Agent] ").color(NamedTextColor.GOLD)
    val notEnoughArgs = Component.text("Not enough arguments!").color(NamedTextColor.RED)
    val noPermission = Component.text("Insufficient Permissions!").color(NamedTextColor.RED)
}

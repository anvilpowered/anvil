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

@file:Suppress("UnstableApiUsage")

package org.anvilpowered.anvil.plugin

import io.papermc.paper.event.server.ServerResourcesLoadEvent
import org.anvilpowered.anvil.paper.command.toPaper
import org.bukkit.plugin.Plugin

class AnvilPaperPlugin(private val plugin: AnvilPlugin) {
    fun registerCommands(bootstrap: Plugin, event: ServerResourcesLoadEvent) {
        plugin.registerCommands { command ->
            event.commands.register(bootstrap, command.toPaper())
        }
    }
}

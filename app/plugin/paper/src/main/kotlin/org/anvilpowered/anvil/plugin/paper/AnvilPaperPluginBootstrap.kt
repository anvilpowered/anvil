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

package org.anvilpowered.anvil.plugin.paper

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.anvilpowered.anvil.core.AnvilApi
import org.anvilpowered.anvil.paper.command.toPaper
import org.anvilpowered.anvil.paper.createPaper
import org.anvilpowered.anvil.plugin.core.AnvilPlugin
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.koin.dsl.koinApplication

class AnvilPaperPluginBootstrap : JavaPlugin(), Listener {

    private lateinit var plugin: AnvilPlugin

    override fun onEnable() {
        val bootstrapPlugin = this
        logger.info { "Registering events" }
        plugin = koinApplication { modules(AnvilApi.createPaper(bootstrapPlugin).module) }.koin.get()
        plugin.enable()
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
            plugin.registerCommands { command ->
                event.registrar().register(command.toPaper())
            }
        }
    }

    override fun onDisable() = plugin.disable()
}

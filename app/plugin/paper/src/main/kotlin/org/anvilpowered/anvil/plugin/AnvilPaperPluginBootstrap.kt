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
import org.anvilpowered.anvil.core.AnvilApi
import org.anvilpowered.anvil.paper.createPaper
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class AnvilPaperPluginBootstrap : JavaPlugin(), Listener {

    private lateinit var plugin: AnvilPaperPlugin

    override fun onEnable() {
        logger.info { "Registering events" }
        server.pluginManager.registerEvents(this, this)
        plugin = koinApplication {
            modules(
                AnvilApi.createPaper(this@AnvilPaperPluginBootstrap).module,
                module { singleOf(::AnvilPaperPlugin) },
            )
        }.koin.get()
    }

    @EventHandler
    fun load(event: ServerResourcesLoadEvent) {
        logger.info { "Load event" }
        plugin.registerCommands(this, event)
    }
}

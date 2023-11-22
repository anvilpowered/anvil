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
                AnvilApi.createPaper().module,
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

@file:Suppress("UnstableApiUsage")

package org.anvilpowered.anvil.plugin

import io.papermc.paper.event.server.ServerResourcesLoadEvent
import org.anvilpowered.anvil.core.AnvilApi
import org.anvilpowered.anvil.paper.createPaper
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class AnvilPaperPluginBootstrap : JavaPlugin(), Listener {

    private val plugin = with(AnvilApi.createPaper()) {
        AnvilPaperPlugin()
    }

    override fun onEnable() {
        logger.info { "Registering events" }
        server.pluginManager.registerEvents(this, this)
    }

    @EventHandler
    fun load(event: ServerResourcesLoadEvent) {
        logger.info { "Load event" }
        plugin.registerCommands(this, event)
    }
}

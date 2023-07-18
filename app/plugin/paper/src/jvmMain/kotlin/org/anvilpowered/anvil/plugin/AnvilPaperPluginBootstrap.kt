@file:Suppress("UnstableApiUsage")

package org.anvilpowered.anvil.plugin

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.event.server.ServerResourcesLoadEvent
import org.anvilpowered.anvil.api.AnvilApi
import org.anvilpowered.anvil.createPaper
import org.anvilpowered.anvil.domain.user.Component
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

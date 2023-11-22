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

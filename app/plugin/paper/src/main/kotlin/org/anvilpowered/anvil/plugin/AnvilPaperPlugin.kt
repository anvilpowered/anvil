@file:Suppress("UnstableApiUsage")

package org.anvilpowered.anvil.plugin

import io.papermc.paper.event.server.ServerResourcesLoadEvent
import org.anvilpowered.anvil.paper.AnvilPaperApi
import org.anvilpowered.anvil.paper.command.toPaper
import org.bukkit.plugin.Plugin

context(AnvilPaperApi)
class AnvilPaperPlugin : AnvilPlugin() {
    fun registerCommands(bootstrap: Plugin, event: ServerResourcesLoadEvent) {
        registerCommands { command ->
            event.commands.register(bootstrap, command.toPaper())
        }
    }
}

@file:Suppress("UnstableApiUsage")

package org.anvilpowered.anvil.plugin

import io.papermc.paper.event.server.ServerResourcesLoadEvent
import org.anvilpowered.anvil.AnvilPaperApi
import org.anvilpowered.anvil.command.toPaper
import org.bukkit.plugin.Plugin

context(AnvilPaperApi)
class AnvilPaperPlugin : AnvilPlugin() {
    fun registerCommands(bootstrap: Plugin, event: ServerResourcesLoadEvent) {
        registerCommands { command ->
            event.commands.register(bootstrap, command.toPaper())
        }
    }
}

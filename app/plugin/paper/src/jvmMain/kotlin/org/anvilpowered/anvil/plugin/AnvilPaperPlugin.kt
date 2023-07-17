@file:Suppress("UnstableApiUsage")

package org.anvilpowered.anvil.plugin

import io.papermc.paper.event.server.ServerResourcesLoadEvent
import org.anvilpowered.anvil.AnvilPaperApi
import org.anvilpowered.anvil.command.toPaperRoot
import org.bukkit.plugin.Plugin

context(AnvilPaperApi)
class AnvilPaperPlugin : AnvilPlugin() {
    fun registerCommands(bootstrap: Plugin, event: ServerResourcesLoadEvent) {
        registerCommands { command ->
            event.commands.register(command.toPaperRoot(bootstrap))
        }
    }
}

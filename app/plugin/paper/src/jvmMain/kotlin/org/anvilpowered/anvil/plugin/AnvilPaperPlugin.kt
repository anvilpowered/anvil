package org.anvilpowered.anvil.plugin

import io.papermc.paper.command.brigadier.CommandBuilder
import org.anvilpowered.anvil.AnvilPaperApi
import org.anvilpowered.anvil.command.toPaper
import org.bukkit.plugin.Plugin

context(AnvilPaperApi)
class AnvilPaperPlugin : AnvilPlugin() {
    fun registerCommands(bootstrap: Plugin) {
        registerCommands { command ->
            CommandBuilder.newCommandBuilder(bootstrap, "test")
                .forward(command.toPaper(), null, false)
                .register()
        }
    }
}

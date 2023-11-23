package org.anvilpowered.anvil.plugin.command

import net.kyori.adventure.text.Component
import org.anvilpowered.anvil.core.command.CommandSource
import org.anvilpowered.anvil.plugin.command.common.addHelp
import org.anvilpowered.anvil.plugin.command.plugin.PluginCommandFactory
import org.anvilpowered.kbrig.builder.ArgumentBuilder
import org.anvilpowered.kbrig.tree.LiteralCommandNode

private val children = mapOf(
    "help" to Component.text("Shows this help message"),
    "plugin" to Component.text("Plugin management"),
    "version" to Component.text("Shows the Anvil Agent version"),
)

class AnvilCommandFactory(private val pluginCommandFactory: PluginCommandFactory) {
    fun create(): LiteralCommandNode<CommandSource> =
        ArgumentBuilder.literal<CommandSource>("anvil")
            .addHelp("anvil", children)
            .then(pluginCommandFactory.create())
            .build()
}

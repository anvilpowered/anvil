package org.anvilpowered.anvil.plugin.command

import org.anvilpowered.anvil.api.AnvilApi
import org.anvilpowered.anvil.domain.command.CommandSource
import org.anvilpowered.anvil.domain.user.Component
import org.anvilpowered.anvil.plugin.command.common.addHelp
import org.anvilpowered.anvil.plugin.command.plugin.PluginCommand
import org.anvilpowered.kbrig.builder.ArgumentBuilder
import org.anvilpowered.kbrig.tree.LiteralCommandNode

private val children = mapOf(
    "help" to Component.text("Shows this help message"),
    "plugin" to Component.text("Plugin management"),
    "version" to Component.text("Shows the Anvil Agent version"),
)

object AnvilCommand {
    context(AnvilApi)
    fun create(): LiteralCommandNode<CommandSource> =
        ArgumentBuilder.literal<CommandSource>("anvil")
            .addHelp("anvil", children)
            .then(PluginCommand.create())
            .build()
}

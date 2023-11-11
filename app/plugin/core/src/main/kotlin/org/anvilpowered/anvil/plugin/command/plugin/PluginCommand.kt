package org.anvilpowered.anvil.plugin.command.plugin

import org.anvilpowered.anvil.api.AnvilApi
import org.anvilpowered.anvil.api.user.requiresPermission
import org.anvilpowered.anvil.domain.command.CommandSource
import org.anvilpowered.anvil.domain.user.Component
import org.anvilpowered.anvil.plugin.command.common.addHelp
import org.anvilpowered.kbrig.builder.ArgumentBuilder
import org.anvilpowered.kbrig.tree.LiteralCommandNode

private val children = mapOf(
    "help" to Component.text("Shows this help message"),
    "list" to Component.text("Lists all plugins"),
    "info <name>" to Component.text("Shows information about a plugin"),
)

object PluginCommand {
    context(AnvilApi)
    fun create(): LiteralCommandNode<CommandSource> =
        ArgumentBuilder.literal<CommandSource>("plugin")
            .addHelp("anvil plugin", children)
            .requiresPermission("anvil.agent.plugin")
            .then(createList())
            .then(createInfo())
            .build()
}

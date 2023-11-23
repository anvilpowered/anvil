package org.anvilpowered.anvil.plugin.command.plugin

import net.kyori.adventure.text.Component
import org.anvilpowered.anvil.core.command.CommandSource
import org.anvilpowered.anvil.core.platform.PluginManager
import org.anvilpowered.anvil.core.user.requiresPermission
import org.anvilpowered.anvil.plugin.command.common.addHelp
import org.anvilpowered.kbrig.builder.ArgumentBuilder
import org.anvilpowered.kbrig.tree.LiteralCommandNode

private val children = mapOf(
    "help" to Component.text("Shows this help message"),
    "list" to Component.text("Lists all plugins"),
    "info <name>" to Component.text("Shows information about a plugin"),
)

class PluginCommandFactory(val pluginManager: PluginManager) {
    fun create(): LiteralCommandNode<CommandSource> =
        ArgumentBuilder.literal<CommandSource>("plugin")
            .addHelp("anvil plugin", children)
            .requiresPermission("anvil.agent.plugin")
            .then(createList())
            .then(createInfo())
            .build()
}

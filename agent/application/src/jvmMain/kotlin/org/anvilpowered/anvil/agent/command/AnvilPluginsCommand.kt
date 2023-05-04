package org.anvilpowered.anvil.agent.command

import net.kyori.adventure.text.format.NamedTextColor
import org.anvilpowered.anvil.agent.platform.PluginManager
import org.anvilpowered.anvil.user.CommandSource
import org.anvilpowered.anvil.user.Component
import org.anvilpowered.kbrig.builder.ArgumentBuilder
import org.anvilpowered.kbrig.builder.executesSingleSuccess
import org.anvilpowered.kbrig.tree.LiteralCommandNode

context(PluginManager.Scope)
fun AnvilCommand.createPlugins(): LiteralCommandNode<CommandSource> {
    return ArgumentBuilder.literal<CommandSource>("plugins")
        .requiresPermission("anvil.agent.plugins")
        .executesSingleSuccess { context ->
            val pluginNamesString = pluginManager.plugins.joinToString(", ") { it.name }
            context.source.sendMessage(Component.text("Plugins: $pluginNamesString").color(NamedTextColor.AQUA))
        }.build()
}

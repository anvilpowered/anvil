package org.anvilpowered.anvil.agent.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.tree.CommandNode
import net.kyori.adventure.text.Component
import org.anvilpowered.anvil.agent.platform.PluginManager
import org.anvilpowered.anvil.user.CommandSource

context(PluginManager.Scope)
fun createPlugins(): CommandNode<CommandSource> =
    LiteralArgumentBuilder.literal<CommandSource>("plugins")
        .requiresPermission("anvil.agent.plugins")
        .executesSingleSuccess { context ->
            val pluginNamesString = pluginManager.plugins.joinToString(", ") { it.name }
            context.source.sendMessage(Component.text("Plugins: $pluginNamesString"))
        }.build()

package org.anvilpowered.anvil.plugin.command.plugin

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.anvilpowered.anvil.core.command.CommandSource
import org.anvilpowered.kbrig.builder.ArgumentBuilder
import org.anvilpowered.kbrig.builder.executesSingleSuccess
import org.anvilpowered.kbrig.tree.LiteralCommandNode

fun PluginCommandBuilder.createList(): LiteralCommandNode<CommandSource> =
    ArgumentBuilder.literal<CommandSource>("list")
        .executesSingleSuccess { context ->
            val pluginNamesString = pluginManager.plugins.joinToString(", ") { it.name }
            context.source.audience.sendMessage(
                Component.text("Plugins: $pluginNamesString").color(NamedTextColor.AQUA),
            )
        }.build()

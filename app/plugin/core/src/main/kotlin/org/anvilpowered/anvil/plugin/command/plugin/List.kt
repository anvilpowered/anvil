package org.anvilpowered.anvil.plugin.command.plugin

import net.kyori.adventure.text.format.NamedTextColor
import org.anvilpowered.anvil.api.AnvilApi
import org.anvilpowered.anvil.domain.command.CommandSource
import org.anvilpowered.anvil.domain.user.Component
import org.anvilpowered.kbrig.builder.ArgumentBuilder
import org.anvilpowered.kbrig.builder.executesSingleSuccess
import org.anvilpowered.kbrig.tree.LiteralCommandNode

context(AnvilApi)
fun PluginCommand.createList(): LiteralCommandNode<CommandSource> =
    ArgumentBuilder.literal<CommandSource>("list")
        .executesSingleSuccess { context ->
            val pluginNamesString = pluginManager.plugins.joinToString(", ") { it.name }
            context.source.audience.sendMessage(
                Component.text("Plugins: $pluginNamesString").color(NamedTextColor.AQUA),
            )
        }.build()

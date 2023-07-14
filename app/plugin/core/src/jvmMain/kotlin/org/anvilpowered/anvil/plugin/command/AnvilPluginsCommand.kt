package org.anvilpowered.anvil.plugin.command

import net.kyori.adventure.text.format.NamedTextColor
import org.anvilpowered.anvil.api.AnvilApi
import org.anvilpowered.anvil.api.user.requiresPermission
import org.anvilpowered.anvil.domain.command.CommandSource
import org.anvilpowered.anvil.domain.user.Component
import org.anvilpowered.kbrig.builder.ArgumentBuilder
import org.anvilpowered.kbrig.builder.executesSingleSuccess
import org.anvilpowered.kbrig.tree.LiteralCommandNode

context(AnvilApi)
fun AnvilCommand.createPlugins(): LiteralCommandNode<CommandSource> =
    ArgumentBuilder.literal<CommandSource>("plugins")
        .requiresPermission("anvil.agent.plugins")
        .executesSingleSuccess { context ->
            val pluginNamesString = pluginManager.plugins.joinToString(", ") { it.name }
            context.source.audience.sendMessage(
                Component.text("Plugins: $pluginNamesString").color(NamedTextColor.AQUA),
            )
        }.build()

package org.anvilpowered.anvil.plugin.command.plugin

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.anvilpowered.anvil.core.AnvilApi
import org.anvilpowered.anvil.core.command.CommandSource
import org.anvilpowered.anvil.plugin.command.common.executesUsage
import org.anvilpowered.kbrig.Command
import org.anvilpowered.kbrig.argument.StringArgumentType
import org.anvilpowered.kbrig.builder.ArgumentBuilder
import org.anvilpowered.kbrig.context.get
import org.anvilpowered.kbrig.tree.LiteralCommandNode

context(AnvilApi)
fun PluginCommand.createInfo(): LiteralCommandNode<CommandSource> =
    ArgumentBuilder.literal<CommandSource>("info")
        .executesUsage("anvil plugin info <name>")
        .then(
            ArgumentBuilder.required<CommandSource, String>("name", StringArgumentType.SingleWord)
                .executes { context ->
                    val name = context.get<String>("name")
                    val plugin = pluginManager.plugins.find { it.name == name }
                    if (plugin == null) {
                        context.source.audience.sendMessage(
                            Component.text()
                                .append(Component.text("Plugin not found: ").color(NamedTextColor.RED))
                                .append(Component.text(name).color(NamedTextColor.WHITE)),
                        )
                        0
                    } else {
                        context.source.audience.sendMessage(
                            Component.text()
                                .append(Component.text("Plugin: ").color(NamedTextColor.AQUA))
                                .append(Component.text(plugin.name).color(NamedTextColor.WHITE)),
                        )
                        Command.SINGLE_SUCCESS
                    }
                }
                .build(),
        ).build()

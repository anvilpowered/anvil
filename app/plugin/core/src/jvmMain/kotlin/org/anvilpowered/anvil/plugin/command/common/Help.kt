package org.anvilpowered.anvil.plugin.command.common

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.anvilpowered.anvil.domain.command.CommandSource
import org.anvilpowered.anvil.plugin.PluginMessages
import org.anvilpowered.kbrig.builder.ArgumentBuilder
import org.anvilpowered.kbrig.builder.executesSingleSuccess

// TODO: Idea: Automatically detect usage from command node tree

fun <B : ArgumentBuilder<CommandSource, B>> B.executesUsage(usage: String): B =
    executes {
        it.source.audience.sendMessage(
            Component.text()
                .append(PluginMessages.pluginPrefix)
                .append(Component.text("Command usage: ", NamedTextColor.GOLD))
                .append(Component.text("/$usage", NamedTextColor.AQUA)),
        )
        0
    }

fun <B : ArgumentBuilder<CommandSource, B>> B.executesUsage(baseName: String, children: Iterable<String>): B =
    executes { context ->
        context.source.audience.sendMessage(
            Component.text()
                .append(PluginMessages.pluginPrefix)
                .append(Component.text("Command usage: ", NamedTextColor.GOLD))
                .append(Component.text("/$baseName", NamedTextColor.GREEN))
                .append(Component.space())
                .append(Component.text("${children.joinToString("|")} ...", NamedTextColor.GREEN))
                .append(Component.newline())
                .append(Component.text("For more information see ", NamedTextColor.AQUA))
                .append(Component.text("/$baseName help", NamedTextColor.GREEN)),
        )
        0
    }

fun <B : ArgumentBuilder<CommandSource, B>> B.addHelpChild(baseName: String, children: Map<String, Component>): B = then(
    ArgumentBuilder.literal<CommandSource>("help").executesSingleSuccess { context ->
        context.source.audience.sendMessage(
            Component.text()
                .append(PluginMessages.pluginPrefix)
                .append(Component.text("Command usage: ", NamedTextColor.GOLD))
                .append(Component.text("/$baseName", NamedTextColor.GREEN))
                .append(Component.newline())
                .append(Component.text("Children:", NamedTextColor.AQUA))
                .append(Component.newline())
                .append(
                    children.map { (command, description) ->
                        Component.text()
                            .append(Component.text(" /$baseName ", NamedTextColor.DARK_GRAY))
                            .append(Component.text(command, NamedTextColor.GREEN))
                            .append(Component.space())
                            .append(description.color(NamedTextColor.GRAY))
                    },
                ),
        )
    },
)

package org.anvilpowered.anvil.core.command.config

import kotlinx.serialization.json.Json
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.anvilpowered.anvil.core.command.CommandSource
import org.anvilpowered.anvil.core.config.serialize
import org.anvilpowered.anvil.core.config.serializeDefault
import org.anvilpowered.kbrig.builder.ArgumentBuilder
import org.anvilpowered.kbrig.tree.LiteralCommandNode

private val prettyJson = Json {
    prettyPrint = true
    encodeDefaults = true
}

fun ConfigCommandFactory.createGet(): LiteralCommandNode<CommandSource> =
    ArgumentBuilder.literal<CommandSource>("get")
        .then(
            keyNamespace.keyArgument { context, key ->
                val defaultValue = registry.serializeDefault(key, prettyJson)
                val currentValue = registry.serialize(key, prettyJson)
                context.source.audience.sendMessage(
                    Component.text()
                        .append(Component.text("Key ").color(NamedTextColor.GREEN))
                        .append(Component.text(key.name).color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                        .append(Component.newline())
                        .append(Component.text("Type: ").color(NamedTextColor.GREEN))
                        .append(Component.text(key.type.type.toString()).color(NamedTextColor.GRAY))
                        .append(Component.newline())
                        .append(Component.text("Default value: ").color(NamedTextColor.GREEN))
                        .append(
                            Component.text(defaultValue).color(NamedTextColor.GRAY)
                                .hoverEvent(Component.text("Click to copy default value").color(NamedTextColor.GRAY))
                                .clickEvent(ClickEvent.copyToClipboard(defaultValue)),
                        )
                        .append(Component.newline())
                        .append(Component.text("Current value: ").color(NamedTextColor.GREEN))
                        .append(
                            Component.text(currentValue).color(NamedTextColor.GRAY)
                                .hoverEvent(Component.text("Click to copy current value").color(NamedTextColor.GRAY))
                                .clickEvent(ClickEvent.copyToClipboard(currentValue)),
                        )
                        .build(),
                )
                1
            },
        ).build()

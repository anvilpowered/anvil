package org.anvilpowered.anvil.core.command.config

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.anvilpowered.anvil.core.command.CommandSource
import org.anvilpowered.anvil.core.config.Key
import org.anvilpowered.anvil.core.config.KeyNamespace
import org.anvilpowered.kbrig.argument.StringArgumentType
import org.anvilpowered.kbrig.builder.ArgumentBuilder
import org.anvilpowered.kbrig.builder.RequiredArgumentBuilder
import org.anvilpowered.kbrig.context.CommandContext
import org.anvilpowered.kbrig.context.get

fun KeyNamespace.keyArgument(
    argumentName: String = "key",
    command: (context: CommandContext<CommandSource>, key: Key<*>) -> Int,
): RequiredArgumentBuilder<CommandSource, String> =
    ArgumentBuilder.required<CommandSource, String>(argumentName, StringArgumentType.SingleWord)
        .suggests { _, builder ->
            println("Suggesting keys")
            keys.filter { it.name.startsWith(builder.input) }.forEach { key ->
                println("Suggesting ${key.name}")
                builder.suggest(key.name)
            }
            builder.build()
        }.executes { context ->
            val keyName = context.get<String>(argumentName)
            keys.find { it.name == keyName }?.let { key ->
                command(context, key)
            } ?: run {
                context.source.audience.sendMessage(
                    Component.text()
                        .append(Component.text("Key with name ", NamedTextColor.RED))
                        .append(Component.text(keyName, NamedTextColor.GOLD))
                        .append(Component.text(" not found!", NamedTextColor.RED))
                        .build(),
                )
                0
            }
        }

/*
 *   Anvil - AnvilPowered.org
 *   Copyright (C) 2019-2024 Contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
            keys.filter { it.name.startsWith(builder.remainingLowerCase, ignoreCase = true) }.forEach { key ->
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

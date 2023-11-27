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

package org.anvilpowered.anvil.plugin.command.common

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.format.NamedTextColor
import org.anvilpowered.anvil.core.command.CommandSource
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

fun <B : ArgumentBuilder<CommandSource, B>> B.addHelp(baseName: String, children: Map<String, Component>): B =
    executes { context ->
        context.source.audience.sendMessage(
            Component.text()
                .append(PluginMessages.pluginPrefix)
                .append(Component.text("Command usage: ", NamedTextColor.GOLD))
                .append(Component.text("/$baseName", NamedTextColor.GREEN))
                .append(Component.space())
                .append(Component.text("${children.keys.joinToString("|")} ...", NamedTextColor.GREEN))
                .append(Component.newline())
                .append(Component.text("For more information see ", NamedTextColor.AQUA))
                .append(Component.text("/$baseName help", NamedTextColor.GREEN)),
        )
        0
    }.then(
        ArgumentBuilder.literal<CommandSource>("help").executesSingleSuccess { context ->
            context.source.audience.sendMessage(
                Component.text()
                    .append(PluginMessages.pluginPrefix)
                    .append(Component.text("Command usage: ", NamedTextColor.GOLD))
                    .append(Component.text("/$baseName", NamedTextColor.GREEN))
                    .append(Component.newline())
                    .append(Component.text("Subcommands:", NamedTextColor.AQUA))
                    .append(Component.newline())
                    .append(
                        Component.join(
                            JoinConfiguration.newlines(),
                            children.map { (command, description) ->
                                Component.text()
                                    .append(Component.text(" /$baseName ", NamedTextColor.DARK_GRAY))
                                    .append(Component.text(command, NamedTextColor.GREEN))
                                    .append(Component.space())
                                    .append(description.color(NamedTextColor.GRAY))
                            },
                        ),
                    ),
            )
        },
    )

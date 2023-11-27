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

package org.anvilpowered.anvil.plugin.command.plugin

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.anvilpowered.anvil.core.command.CommandSource
import org.anvilpowered.anvil.plugin.command.common.executesUsage
import org.anvilpowered.kbrig.Command
import org.anvilpowered.kbrig.argument.StringArgumentType
import org.anvilpowered.kbrig.builder.ArgumentBuilder
import org.anvilpowered.kbrig.context.get
import org.anvilpowered.kbrig.tree.LiteralCommandNode

fun PluginCommandFactory.createInfo(): LiteralCommandNode<CommandSource> =
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

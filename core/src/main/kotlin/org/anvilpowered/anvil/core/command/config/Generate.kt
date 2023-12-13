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
import org.anvilpowered.anvil.core.config.DefaultRegistry
import org.anvilpowered.anvil.core.config.fullName
import org.anvilpowered.kbrig.argument.StringArgumentType
import org.anvilpowered.kbrig.builder.ArgumentBuilder
import org.anvilpowered.kbrig.context.get
import org.anvilpowered.kbrig.tree.LiteralCommandNode
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.pathString

fun ConfigCommandFactory.createGenerate(): LiteralCommandNode<CommandSource> =
    ArgumentBuilder.literal<CommandSource>("generate")
        .executes { context ->
            context.source.audience.sendMessage(
                Component.text()
                    .append(Component.text("Please specify configuration format. Available: ", NamedTextColor.GREEN))
                    .append(Component.text(exporters.joinToString(", ") { it.type.fullName }, NamedTextColor.GOLD))
                    .build(),
            )
            0
        }
        .then(
            ArgumentBuilder.required<CommandSource, String>("type", StringArgumentType.SingleWord)
                .suggests { _, builder ->
                    println("Suggestions for ${builder.remainingLowerCase}")
                    exporters
                        .filter { it.type.name.startsWith(builder.remainingLowerCase, ignoreCase = true) }
                        .forEach { builder.suggest(it.type.name) }
                    builder.build()
                }.executesWithExporter(force = false)
                .then(ArgumentBuilder.literal<CommandSource>("--force").executesWithExporter(force = true))
                .build(),
        ).build()

context(ConfigCommandFactory)
private fun <B : ArgumentBuilder<CommandSource, B>> B.executesWithExporter(force: Boolean): B = executes { context ->
    val targetType = context.get<String>("type")
    exporters.find { it.type.name == targetType }?.let { exporter ->
        if (exporter.configPath.exists()) {
            if (force) {
                context.source.audience.sendMessage(
                    Component.text()
                        .append(Component.text("File ", NamedTextColor.GREEN))
                        .append(Component.text(exporter.configPath.pathString, NamedTextColor.GOLD))
                        .append(Component.text(" already exists! Overwriting because of --force!", NamedTextColor.GREEN))
                        .build(),
                )
                if (!exporter.configPath.deleteIfExists()) {
                    Component.text()
                        .append(Component.text("File ", NamedTextColor.RED))
                        .append(Component.text(exporter.configPath.pathString, NamedTextColor.GOLD))
                        .append(Component.text(" could not be deleted!", NamedTextColor.RED))
                        .build()
                }
            } else {
                context.source.audience.sendMessage(
                    Component.text()
                        .append(Component.text("File ", NamedTextColor.RED))
                        .append(Component.text(exporter.configPath.pathString, NamedTextColor.GOLD))
                        .append(Component.text(" already exists! Use --force to overwrite.", NamedTextColor.RED))
                        .build(),
                )
                return@let 0
            }
        }
        context.source.audience.sendMessage(
            Component.text()
                .append(Component.text("Generating file ", NamedTextColor.GREEN))
                .append(Component.text(exporter.configPath.pathString, NamedTextColor.GOLD))
                .build(),
        )
        exporter.export(DefaultRegistry)
        context.source.audience.sendMessage(
            Component.text()
                .append(Component.text("File ", NamedTextColor.GREEN))
                .append(Component.text(exporter.configPath.pathString, NamedTextColor.GOLD))
                .append(Component.text(" generated!", NamedTextColor.GREEN))
                .build(),
        )
        1
    } ?: run {
        context.source.audience.sendMessage(
            Component.text()
                .append(Component.text("Configurate exporter for file type ", NamedTextColor.RED))
                .append(Component.text(targetType, NamedTextColor.GOLD))
                .append(Component.text(" not found!", NamedTextColor.RED))
                .build(),
        )
        0
    }
}

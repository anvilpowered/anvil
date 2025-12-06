/*
 *   Anvil - AnvilPowered.org
 *   Copyright (C) 2019-2026 Contributors
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
import org.anvilpowered.anvil.core.config.ConfigurateRegistryExporter
import org.anvilpowered.anvil.core.config.DefaultRegistry
import org.anvilpowered.anvil.core.config.fullName
import org.anvilpowered.kbrig.argument.StringArgumentType
import org.anvilpowered.kbrig.builder.ArgumentBuilder
import org.anvilpowered.kbrig.context.CommandContextScopeDsl
import org.anvilpowered.kbrig.context.CommandExecutionScope
import org.anvilpowered.kbrig.context.executesScoped
import org.anvilpowered.kbrig.context.get
import org.anvilpowered.kbrig.context.yieldError
import org.anvilpowered.kbrig.tree.LiteralCommandNode
import kotlin.io.path.deleteIfExists
import kotlin.io.path.pathString

fun ConfigCommandFactory.createGenerate(): LiteralCommandNode<CommandSource> =
  ArgumentBuilder
    .literal<CommandSource>("generate")
    .executes { context ->
      context.source.sendMessage(
        Component
          .text()
          .append(Component.text("Please specify configuration format. Available: ", NamedTextColor.GREEN))
          .append(Component.text(exporters.joinToString(", ") { it.type.fullName }, NamedTextColor.GOLD))
          .build(),
      )
      0
    }.then(
      ArgumentBuilder
        .required<CommandSource, String>("type", StringArgumentType.SingleWord)
        .suggests { _, builder ->
          exporters
            .filter { it.type.name.startsWith(builder.remainingLowerCase, ignoreCase = true) }
            .forEach { builder.suggest(it.type.name) }
          builder.build()
        }.executesWithExporter(force = false)
        .then(ArgumentBuilder.literal<CommandSource>("--force").executesWithExporter(force = true))
        .build(),
    ).build()

context(ConfigCommandFactory)
@CommandContextScopeDsl
private suspend fun CommandExecutionScope<CommandSource>.extractExporterArgument(
  argumentName: String = "type",
): ConfigurateRegistryExporter {
  val targetType = context.get<String>(argumentName)
  val exporter =
    exporters.find { it.type.name == targetType } ?: run {
      context.source.sendMessage(
        Component
          .text()
          .append(Component.text("Configurate exporter for file type ", NamedTextColor.RED))
          .append(Component.text(targetType, NamedTextColor.GOLD))
          .append(Component.text(" not found!", NamedTextColor.RED))
          .build(),
      )
      yieldError()
    }
  return exporter
}

context(ConfigCommandFactory)
private fun <B : ArgumentBuilder<CommandSource, B>> B.executesWithExporter(force: Boolean): B =
  executesScoped {
    val exporter = extractExporterArgument()
    val newType = exporter.type
    val newPath = exporter.configPath.pathString
    val configurateRegistry = configurateRegistryClosure.discover()
    if (configurateRegistry != null) {
      val existingType = configurateRegistry.type
      val existingPath = configurateRegistry.path.pathString
      if (force) {
        context.source.sendMessage(
          Component
            .text()
            .append(Component.text("File ", NamedTextColor.YELLOW))
            .append(Component.text(existingPath, NamedTextColor.GOLD))
            .append(Component.text(" already exists! ", NamedTextColor.YELLOW))
            .append(Component.newline())
            .append(
              Component.text(
                if (existingType == newType) {
                  "Overwriting because of --force!"
                } else {
                  "Replacing with $newPath because of --force!"
                },
                NamedTextColor.YELLOW,
              ),
            ).build(),
        )
        if (!configurateRegistry.path.deleteIfExists()) {
          Component
            .text()
            .append(Component.text("File ", NamedTextColor.RED))
            .append(Component.text(existingPath, NamedTextColor.GOLD))
            .append(Component.text(" could not be deleted!", NamedTextColor.RED))
            .build()
        }
      } else {
        context.source.sendMessage(
          Component
            .text()
            .append(Component.text("File ", NamedTextColor.RED))
            .append(Component.text(existingPath, NamedTextColor.GOLD))
            .append(Component.text(" already exists!", NamedTextColor.RED))
            .append(Component.newline())
            .append(Component.text("Use --force to ", NamedTextColor.RED))
            .append(
              if (existingType == newType) {
                Component.text("overwrite.", NamedTextColor.RED)
              } else {
                Component
                  .text()
                  .append(Component.text("replace with ", NamedTextColor.RED))
                  .append(Component.text(newPath, NamedTextColor.GOLD))
                  .append(Component.text(".", NamedTextColor.RED))
              },
            ).build(),
        )
        yieldError()
      }
    }
    exporter.export(DefaultRegistry, serializers)
    context.source.sendMessage(
      Component
        .text()
        .append(Component.text("Generated ", NamedTextColor.GREEN))
        .append(Component.text(newPath, NamedTextColor.GOLD))
        .append(Component.text("!", NamedTextColor.GREEN))
        .append(Component.newline())
        .append(Component.text("Please restart the server to apply changes.", NamedTextColor.DARK_GREEN))
        .build(),
    )
  }

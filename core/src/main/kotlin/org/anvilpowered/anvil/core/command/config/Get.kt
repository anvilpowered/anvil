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
        context.source.sendMessage(
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

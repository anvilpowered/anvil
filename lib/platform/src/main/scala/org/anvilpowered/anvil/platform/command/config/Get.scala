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

package org.anvilpowered.anvil.platform.command.config

import cats.Monad
import cats.data.EitherT
import cats.effect.{Async, IO, Sync}
import cats.syntax.all.*
import org.anvilpowered.anvil.platform.command.{CommandSource, KeyArgument}
import org.anvilpowered.anvil.command.tree.LiteralCommandNode
import org.anvilpowered.anvil.command.builder.ArgumentBuilder
import org.anvilpowered.anvil.config.KeyNamespace
import org.anvilpowered.anvil.command.context.CommandContext
import org.anvilpowered.anvil.config.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.event.ClickEvent

extension (factory: ConfigCommandFactory) {
  def createGet(using KeyNamespace): LiteralCommandNode[CommandSource] =
    ArgumentBuilder
      .literal[CommandSource]("get")
      .thenArg(
        KeyArgument.simpleBuilderUntyped[
          CommandSource,
        ]([F[_]] =>
          (context: CommandContext[CommandSource], key: Key[?]) =>
            (F: Async[F]) ?=> {
              EitherT.liftF(F.delay {
                val defaultValue = key.codec(factory.registry.getDefault(key)).spaces2
                val currentValue = key.codec(factory.registry(key)).spaces2
                Component
                  .text()
                  .append(Component.text("Key ").color(NamedTextColor.GREEN))
                  .append(Component.text(key.name).color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                  .append(Component.newline())
                  .append(Component.text("Type: ").color(NamedTextColor.GREEN))
                  .append(Component.text(key.typeToken.getType.toString).color(NamedTextColor.GRAY))
                  .append(Component.newline())
                  .append(Component.text("Default value: ").color(NamedTextColor.GREEN))
                  .append(
                    Component
                      .text(defaultValue)
                      .color(NamedTextColor.GRAY)
                      .hoverEvent(Component.text("Click to copy default value").color(NamedTextColor.GRAY))
                      .clickEvent(ClickEvent.copyToClipboard(defaultValue)),
                  )
                  .append(Component.newline())
                  .append(Component.text("Current value: ").color(NamedTextColor.GREEN))
                  .append(
                    Component
                      .text(currentValue)
                      .color(NamedTextColor.GRAY)
                      .hoverEvent(Component.text("Click to copy current value").color(NamedTextColor.GRAY))
                      .clickEvent(ClickEvent.copyToClipboard(currentValue)),
                  )
                  .build()
              })
            },
        ),
      )
      .build()
}

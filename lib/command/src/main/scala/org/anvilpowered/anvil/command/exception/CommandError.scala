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

package org.anvilpowered.anvil.command.exception

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

import java.lang.reflect.Type

trait CommandError {
  def text: Component
}

trait ArgumentError extends CommandError {
  def argument: String
}

case class NoArgError(override val argument: String) extends ArgumentError {
  override val text: Component = Component
    .text()
    .append(Component.text("No such argument ").color(NamedTextColor.RED))
    .append(Component.text(argument).color(NamedTextColor.GOLD))
    .append(Component.text(" exists on this command").color(NamedTextColor.RED))
    .build()
}

case class NotFoundError(argType: String, override val argument: String) extends ArgumentError {
  override def text: Component = Component
    .text()
    .append(Component.text(s"Could not find $argType ").color(NamedTextColor.RED))
    .append(Component.text(argument).color(NamedTextColor.GOLD))
    .append(Component.text("!").color(NamedTextColor.RED))
    .build()
}

case class AlreadyExistsReplaceError(argType: String, override val argument: String, newPath: Option[String]) extends ArgumentError {
  override def text: Component = Component
    .text()
    .append(Component.text(s"${argType.capitalize} ").color(NamedTextColor.RED))
    .append(Component.text(argument).color(NamedTextColor.GOLD))
    .append(Component.text(" already exists!").color(NamedTextColor.RED))
    .append(Component.newline())
    .append(Component.text("Use --force to ", NamedTextColor.RED))
    .append(
      newPath match {
        case Some(value) =>
          Component
            .text()
            .append(Component.text("replace with ", NamedTextColor.RED))
            .append(Component.text(value, NamedTextColor.GOLD))
            .append(Component.text(".", NamedTextColor.RED))
        case None =>
          Component.text("overwrite.", NamedTextColor.RED)
      },
    )
    .build()
}

case class CouldNotDeleteError(argType: String, override val argument: String) extends ArgumentError {
  override def text: Component = Component
    .text()
    .append(Component.text(s"Could not delete $argType ").color(NamedTextColor.RED))
    .append(Component.text(argument).color(NamedTextColor.GOLD))
    .append(Component.text("!").color(NamedTextColor.RED))
    .build()
}

case class ArgTypeCastError(argType: String, expected: Type, actual: Type, override val argument: String) extends ArgumentError {
  override def text: Component = Component
    .text()
    .append(Component.text(s"Expected $argType argument $argument of type ").color(NamedTextColor.RED))
    .append(Component.text(expected.getTypeName).color(NamedTextColor.GOLD))
    .append(Component.text(" but was "))
    .append(Component.text(actual.getTypeName).color(NamedTextColor.GOLD))
    .append(Component.text("!").color(NamedTextColor.RED))
    .build()
}

case class ParseError(input: String, message: String) extends CommandError {
  val text: Component = Component
    .text()
    .append(Component.text("Could not parse").color(NamedTextColor.RED))
    .append(Component.text(input).color(NamedTextColor.GOLD))
    .append(Component.text(": " + message).color(NamedTextColor.RED))
    .build()
}

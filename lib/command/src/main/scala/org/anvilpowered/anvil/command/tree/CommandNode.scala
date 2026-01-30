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
package org.anvilpowered.anvil.command.tree

import cats.data.{EitherT, Kleisli}
import cats.effect.IO
import org.anvilpowered.anvil.command.{Command, StringReader}
import org.anvilpowered.anvil.command.builder.ArgumentBuilder
import org.anvilpowered.anvil.command.context.CommandContext
import org.anvilpowered.anvil.command.exception.ArgumentError
import org.anvilpowered.anvil.command.suggestion.{SuggestionProvider, Suggestions}

import scala.collection.mutable
import scala.math

abstract class CommandNode[S](
    val name: String,
    val command: Option[Command[S]],
    val requirement: S => Boolean,
    val redirect: Option[CommandNode[S]],
    val forks: Boolean,
    val children: Map[String, CommandNode[S]],
) extends SuggestionProvider[S] {

  private val arguments = mutable.Map[String, ArgumentCommandNode[S, ?]]()
  private var hasLiterals = false

  val usageText: String
  val examples: Set[String]

  def toBuilder: ArgumentBuilder[S]
}

object CommandNode {
  given nameOrdering: Ordering[CommandNode[?]] = Ordering.by[CommandNode[?], String](_.name)
}

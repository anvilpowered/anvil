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

import cats.Monad
import cats.data.{EitherT, Kleisli, ReaderT}
import cats.effect.{Async, Concurrent, IO}
import org.anvilpowered.anvil.command.Command
import org.anvilpowered.anvil.command.argument.ArgumentType
import org.anvilpowered.anvil.command.builder.RequiredArgumentBuilder
import org.anvilpowered.anvil.command.context.CommandContext
import org.anvilpowered.anvil.command.exception.ArgumentError
import org.anvilpowered.anvil.command.suggestion.{SuggestionProvider, Suggestions}
import org.anvilpowered.anvil.command.suggestion.Suggestions.SuggestT

class ArgumentCommandNode[S, T](
    name: String,
    val argType: ArgumentType[S, T],
    command: Option[Command[S]],
    requirement: S => Boolean,
    redirect: Option[CommandNode[S]] = None,
    forks: Boolean = false,
    children: Map[String, CommandNode[S]],
    val customSuggestions: Option[SuggestionProvider[S]] = None,
) extends CommandNode[S](name, command, requirement, redirect, forks, children) {

  override val usageText: String = '<' +: name :+ '>'

  override val examples: Set[String] = argType.examples

  override def suggest[F[_]: Async](context: CommandContext[S]): SuggestT[F] = {
    customSuggestions.getOrElse(argType.suggestionProvider).suggest(context)
  }

  override def toBuilder: RequiredArgumentBuilder[S, T] = {
    RequiredArgumentBuilder[S, T](name, argType)
      .requires(requirement)
      .forward(redirect, forks)
      .suggestsOption(customSuggestions)
      .executesOption(command)
  }

  override def toString: String = s"[argument $name:$argType]"
}

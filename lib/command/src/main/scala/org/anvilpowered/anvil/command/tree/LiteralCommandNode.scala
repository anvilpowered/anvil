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
import org.anvilpowered.anvil.command.suggestion.Suggestions.{Input, SuggestT}
import cats.effect.kernel.Async
import org.anvilpowered.anvil.command.Command
import org.anvilpowered.anvil.command.builder.LiteralArgumentBuilder
import org.anvilpowered.anvil.command.context.CommandContext
import org.anvilpowered.anvil.command.suggestion.Suggestions

class LiteralCommandNode[S](
    literal: String,
    command: Option[Command[S]],
    requirement: S => Boolean,
    redirect: Option[CommandNode[S]],
    forks: Boolean,
    children: Map[String, CommandNode[S]] = Map(),
) extends CommandNode[S](literal, command, requirement, redirect, forks, children) {

  override val usageText: String = name
  private val literalLowerCase: String = name.toLowerCase

  def suggest[F[_]: Async](context: CommandContext[S]): SuggestT[F] = {
    for {
      input <- SuggestT.ask[F, Input]
      result <-
        if (literalLowerCase.regionMatches(0, input.text, 0, input.text.length)) {
          Suggestions.ofOne(name)
        } else {
          SuggestT.pure(Suggestions.Empty)
        }
    } yield result
  }
  override def toBuilder: LiteralArgumentBuilder[S] =
    LiteralArgumentBuilder(name)
      .requires(requirement)
      .forward(redirect, forks)
      .executesOption(command)

  override val examples: Set[String] = Set(name)
  override def toString: String = s"<literal $name>"
}

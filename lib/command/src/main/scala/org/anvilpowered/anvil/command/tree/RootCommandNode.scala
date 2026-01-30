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
import cats.effect.Concurrent
import org.anvilpowered.anvil.command.suggestion.Suggestions.SuggestT
import cats.effect.kernel.Async
import org.anvilpowered.anvil.command.builder.ArgumentBuilder
import org.anvilpowered.anvil.command.context.CommandContext
import org.anvilpowered.anvil.command.suggestion.Suggestions

class RootCommandNode[S](children: Map[String, CommandNode[S]])
    extends CommandNode[S](
      name = "",
      command = None,
      requirement = _ => true,
      redirect = None,
      forks = false,
      children,
    ) {

  override val usageText: String = ""

  def suggest[F[_]: Async](context: CommandContext[S]): SuggestT[F] = SuggestT.pure(Suggestions.Empty)

  override def toBuilder: ArgumentBuilder[S] = {
    throw UnsupportedOperationException("Cannot convert root into a builder")
  }

  override val examples: Set[String] = Set()

  override def toString: String = "<root>"
}

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
package org.anvilpowered.anvil.command.builder

import cats.effect.Async
import org.anvilpowered.anvil.command.argument.ArgumentType
import org.anvilpowered.anvil.command.context.CommandContext
import org.anvilpowered.anvil.command.suggestion.Suggestions.SuggestT
import org.anvilpowered.anvil.command.suggestion.SuggestionProvider
import org.anvilpowered.anvil.command.tree.ArgumentCommandNode

class RequiredArgumentBuilder[S, T](
    val name: String,
    private val argType: ArgumentType[S, T],
) extends ArgumentBuilder[S] {
  private var suggestionsProvider: Option[SuggestionProvider[S]] = None

  def suggestsOption(suggestionsProvider: Option[SuggestionProvider[S]]): this.type = {
    this.suggestionsProvider = suggestionsProvider
    this
  }

  def suggests0(suggestionsProvider: SuggestionProvider[S]): this.type = suggestsOption(Some(suggestionsProvider))

  def suggests(suggestionsProvider: [F[_]: Async] => CommandContext[S] => SuggestT[F]): this.type =
    suggests0(new SuggestionProvider[S] {
      override def suggest[F[_]: Async](context: CommandContext[S]): SuggestT[F] = suggestionsProvider[F](context)
    })

  override def build(): ArgumentCommandNode[S, T] =
    ArgumentCommandNode(name, argType, command, requirement, forward, forks, children, suggestionsProvider)
}

/*
 *   KBrig - AnvilPowered.org
 *   Copyright (c) 2023 Contributors
 *
 *     Use of this source code is governed by an MIT-style license that can be found
 *     in the LICENSE file or at https://opensource.org/licenses/MIT.
 */
package org.anvilpowered.anvil.core.kbrig.builder

import cats.effect.Async
import org.anvilpowered.anvil.core.kbrig.argument.ArgumentType
import org.anvilpowered.anvil.core.kbrig.context.CommandContext
import org.anvilpowered.anvil.core.kbrig.suggestion.SuggestionProvider
import org.anvilpowered.anvil.core.kbrig.suggestion.Suggestions.SuggestT
import org.anvilpowered.anvil.core.kbrig.tree.ArgumentCommandNode

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

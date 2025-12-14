/*
 *   KBrig - AnvilPowered.org
 *   Copyright (c) 2023 Contributors
 *
 *     Use of this source code is governed by an MIT-style license that can be found
 *     in the LICENSE file or at https://opensource.org/licenses/MIT.
 */
package org.anvilpowered.anvil.core.kbrig.builder

import org.anvilpowered.anvil.core.kbrig.argument.ArgumentType
import org.anvilpowered.anvil.core.kbrig.suggestion.SuggestionProvider
import org.anvilpowered.anvil.core.kbrig.tree.ArgumentCommandNode

class RequiredArgumentBuilder[S, T](
    val name: String,
    private val argType: ArgumentType[S, T],
) extends ArgumentBuilder[S] {
  private var suggestionsProvider: Option[SuggestionProvider[S]] = None

  def suggests(provider: Option[SuggestionProvider[S]]): this.type = {
    suggestionsProvider = provider
    this
  }
  def suggests(provider: SuggestionProvider[S]): this.type = suggests(Some(provider))

  override def build(): ArgumentCommandNode[S, T] =
    ArgumentCommandNode(name, argType, command, requirement, forward, forks, children, suggestionsProvider)
}

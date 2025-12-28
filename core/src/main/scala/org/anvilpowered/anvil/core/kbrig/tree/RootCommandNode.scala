/*
 *   KBrig - AnvilPowered.org
 *   Copyright (c) 2023 Contributors
 *
 *     Use of this source code is governed by an MIT-style license that can be found
 *     in the LICENSE file or at https://opensource.org/licenses/MIT.
 */
package org.anvilpowered.anvil.core.kbrig.tree

import cats.Monad
import cats.effect.Concurrent
import org.anvilpowered.anvil.core.kbrig.builder.ArgumentBuilder
import org.anvilpowered.anvil.core.kbrig.context.CommandContext
import org.anvilpowered.anvil.core.kbrig.suggestion.Suggestions
import org.anvilpowered.anvil.core.kbrig.suggestion.Suggestions.SuggestT

class RootCommandNode[S](children: Map[String, CommandNode[S]])
    extends CommandNode[S](
      name = "",
      command = null,
      requirement = _ => true,
      redirect = null,
      forks = false,
      children,
    ) {

  override val usageText: String = ""

  def suggest[F[_]: Concurrent](context: CommandContext[S]): SuggestT[F] = SuggestT.pure(Suggestions.Empty)

  override def toBuilder: ArgumentBuilder[S] = {
    throw UnsupportedOperationException("Cannot convert root into a builder")
  }

  override val examples: Set[String] = Set()

  override def toString: String = "<root>"
}

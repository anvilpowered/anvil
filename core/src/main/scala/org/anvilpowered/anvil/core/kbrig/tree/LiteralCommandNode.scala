/*
 *   KBrig - AnvilPowered.org
 *   Copyright (c) 2023 Contributors
 *
 *     Use of this source code is governed by an MIT-style license that can be found
 *     in the LICENSE file or at https://opensource.org/licenses/MIT.
 */
package org.anvilpowered.anvil.core.kbrig.tree

import cats.Monad
import org.anvilpowered.anvil.core.kbrig.Command
import org.anvilpowered.anvil.core.kbrig.builder.LiteralArgumentBuilder
import org.anvilpowered.anvil.core.kbrig.context.CommandContext
import org.anvilpowered.anvil.core.kbrig.suggestion.SuggestionProvider.SuggestT
import org.anvilpowered.anvil.core.kbrig.suggestion.Suggestions

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

  def suggest[F[_]: Monad](context: CommandContext[S]): SuggestT[F] = {
    for {
      input <- SuggestT.ask[F, String]
      result <-
        if (literalLowerCase.regionMatches(0, input, 0, input.length)) {
          Suggestions.ofOne(name)
        } else {
          SuggestT.pure(Suggestions.empty)
        }
    } yield result
  }
  override def toBuilder: LiteralArgumentBuilder[S] =
    LiteralArgumentBuilder(name)
      .requires(requirement)
      .forward(redirect, forks)
      .executes(command)

  override val examples: Set[String] = Set(name)
  override def toString: String = s"<literal $name>"
}

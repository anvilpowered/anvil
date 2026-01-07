/*
 *   KBrig - AnvilPowered.org
 *   Copyright (c) 2023 Contributors
 *
 *     Use of this source code is governed by an MIT-style license that can be found
 *     in the LICENSE file or at https://opensource.org/licenses/MIT.
 */
package org.anvilpowered.anvil.core.kbrig.tree

import cats.Monad
import cats.data.{EitherT, Kleisli, ReaderT}
import cats.effect.{Async, Concurrent, IO}
import org.anvilpowered.anvil.core.kbrig.Command
import org.anvilpowered.anvil.core.kbrig.argument.ArgumentType
import org.anvilpowered.anvil.core.kbrig.builder.RequiredArgumentBuilder
import org.anvilpowered.anvil.core.kbrig.context.CommandContext
import org.anvilpowered.anvil.core.kbrig.exception.ArgumentError
import org.anvilpowered.anvil.core.kbrig.suggestion.Suggestions.SuggestT
import org.anvilpowered.anvil.core.kbrig.suggestion.{SuggestionProvider, Suggestions}

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
      .suggests(customSuggestions)
      .executes(command)
  }

  override def toString: String = s"[argument $name:$argType]"
}

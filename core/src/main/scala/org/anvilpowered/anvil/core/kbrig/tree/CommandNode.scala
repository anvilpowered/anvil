/*
 *   KBrig - AnvilPowered.org
 *   Copyright (c) 2023 Contributors
 *
 *     Use of this source code is governed by an MIT-style license that can be found
 *     in the LICENSE file or at https://opensource.org/licenses/MIT.
 */
package org.anvilpowered.anvil.core.kbrig.tree

import cats.data.{EitherT, Kleisli}
import cats.effect.IO
import org.anvilpowered.anvil.core.kbrig.Command
import org.anvilpowered.anvil.core.kbrig.StringReader
import org.anvilpowered.anvil.core.kbrig.builder.ArgumentBuilder
import org.anvilpowered.anvil.core.kbrig.context.CommandContext
import org.anvilpowered.anvil.core.kbrig.exception.ArgumentError
import org.anvilpowered.anvil.core.kbrig.suggestion.{SuggestionProvider, Suggestions}

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

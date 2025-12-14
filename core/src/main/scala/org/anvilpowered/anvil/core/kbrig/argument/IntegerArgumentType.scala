/*
 *   KBrig - AnvilPowered.org
 *   Copyright (c) 2023 Contributors
 *
 *     Use of this source code is governed by an MIT-style license that can be found
 *     in the LICENSE file or at https://opensource.org/licenses/MIT.
 */
package org.anvilpowered.anvil.core.kbrig.argument

import cats.Monad
import cats.data.{Kleisli, StateT}
import cats.effect.IO
import org.anvilpowered.anvil.core.kbrig.StringReader
import org.anvilpowered.anvil.core.kbrig.StringReader.ParseT
import org.anvilpowered.anvil.core.kbrig.context.CommandContext
import org.anvilpowered.anvil.core.kbrig.exception.{ArgumentError, CommandSyntaxException}
import org.anvilpowered.anvil.core.kbrig.suggestion.SuggestionProvider

object IntegerArgumentType {

  val all = ArgumentType(
    IntegerArgumentParser(Int.MinValue, Int.MaxValue),
    SuggestionProvider.empty,
    Set(),
  )

  def of[S](
      min: Integer,
      max: Integer,
      suggestionProvider: SuggestionProvider[S] = SuggestionProvider.empty,
      examples: Set[String] = Set(),
  ) = ArgumentType(
    IntegerArgumentParser(min, max),
    suggestionProvider,
    examples,
  )
}

class IntegerArgumentParser(
    override val min: Int,
    override val max: Int,
) extends RangedArgumentParser[Int] {
  override def parse[F[_]: Monad]: ParseT[F, Int] =
    RangedArgumentParser.validateArgument(min, max, StringReader.readInt)
}

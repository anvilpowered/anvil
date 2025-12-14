/*
 *   KBrig - AnvilPowered.org
 *   Copyright (c) 2023 Contributors
 *
 *     Use of this source code is governed by an MIT-style license that can be found
 *     in the LICENSE file or at https://opensource.org/licenses/MIT.
 */
package org.anvilpowered.anvil.core.kbrig.argument

import cats.Monad
import cats.data.Kleisli
import cats.effect.IO
import org.anvilpowered.anvil.core.kbrig.StringReader
import org.anvilpowered.anvil.core.kbrig.StringReader.ParseT
import org.anvilpowered.anvil.core.kbrig.context.CommandContext
import org.anvilpowered.anvil.core.kbrig.exception.{ArgumentError, CommandSyntaxException}
import org.anvilpowered.anvil.core.kbrig.suggestion.SuggestionProvider

object DoubleArgumentType {

  val all = ArgumentType(
    DoubleArgumentParser(Double.MinValue, Double.MaxValue),
    SuggestionProvider.empty,
    Set(),
  )

  def of[S](
      min: Double,
      max: Double,
      suggestionProvider: SuggestionProvider[S] = SuggestionProvider.empty,
      examples: Set[String] = Set(),
  ) = ArgumentType(
    DoubleArgumentParser(min, max),
    suggestionProvider,
    examples,
  )
}

class DoubleArgumentParser(
    override val min: Double,
    override val max: Double,
) extends RangedArgumentParser[Double] {
  override def parse[F[_]: Monad]: ParseT[F, Double] =
    RangedArgumentParser.validateArgument(min, max, StringReader.readDouble)
}

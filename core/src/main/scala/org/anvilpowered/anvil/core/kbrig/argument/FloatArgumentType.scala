/*
 *   KBrig - AnvilPowered.org
 *   Copyright (c) 2023 Contributors
 *
 *     Use of this source code is governed by an MIT-style license that can be found
 *     in the LICENSE file or at https://opensource.org/licenses/MIT.
 */
package org.anvilpowered.anvil.core.kbrig.argument

import cats.Monad
import org.anvilpowered.anvil.core.kbrig.StringReader
import org.anvilpowered.anvil.core.kbrig.StringReader.ParseT
import org.anvilpowered.anvil.core.kbrig.context.CommandContext
import org.anvilpowered.anvil.core.kbrig.exception.CommandSyntaxException
import org.anvilpowered.anvil.core.kbrig.suggestion.SuggestionProvider

object FloatArgumentType {

  val all = ArgumentType(
    FloatArgumentParser(Float.MinValue, Float.MaxValue),
    SuggestionProvider.Empty,
    Set(),
  )

  def of[S](
      min: Float,
      max: Float,
      suggestionProvider: SuggestionProvider[S] = SuggestionProvider.Empty,
      examples: Set[String] = Set(),
  ) = ArgumentType(
    FloatArgumentParser(min, max),
    suggestionProvider,
    examples,
  )
}

class FloatArgumentParser(
    override val min: Float,
    override val max: Float,
) extends RangedArgumentParser[Float] {
  override def parse[F[_]: Monad]: ParseT[F, Float] =
    RangedArgumentParser.validateArgument(min, max, StringReader.readFloat)
}

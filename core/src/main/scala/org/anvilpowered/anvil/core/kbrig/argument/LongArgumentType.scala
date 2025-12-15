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

object LongArgumentType {

  val all = ArgumentType(
    LongArgumentParser(Long.MinValue, Long.MaxValue),
    SuggestionProvider.Empty,
    Set(),
  )

  def of[S](
      min: Long,
      max: Long,
      suggestionProvider: SuggestionProvider[S] = SuggestionProvider.Empty,
      examples: Set[String] = Set(),
  ) = ArgumentType(
    LongArgumentParser(min, max),
    suggestionProvider,
    examples,
  )
}

class LongArgumentParser(
    override val min: Long,
    override val max: Long,
) extends RangedArgumentParser[Long] {
  override def parse[F[_]: Monad]: ParseT[F, Long] =
    RangedArgumentParser.validateArgument(min, max, StringReader.readLong)
}

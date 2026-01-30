/*
 *   Anvil - AnvilPowered.org
 *   Copyright (C) 2019-2026 Contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.anvilpowered.anvil.command.argument

import cats.Monad
import cats.data.Kleisli
import cats.effect.IO
import org.anvilpowered.anvil.command.StringReader
import org.anvilpowered.anvil.command.context.CommandContext
import StringReader.ParseT
import org.anvilpowered.anvil.command.exception.{ArgumentError, CommandSyntaxException}
import org.anvilpowered.anvil.command.suggestion.SuggestionProvider

object DoubleArgumentType {

  val all = ArgumentType(
    DoubleArgumentParser(Double.MinValue, Double.MaxValue),
    SuggestionProvider.Empty,
    Set(),
  )

  def of[S](
      min: Double,
      max: Double,
      suggestionProvider: SuggestionProvider[S] = SuggestionProvider.Empty,
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

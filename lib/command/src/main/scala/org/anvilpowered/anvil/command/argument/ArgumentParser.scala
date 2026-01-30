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
import cats.data.EitherT
import org.anvilpowered.anvil.command.exception.ParseError
import org.anvilpowered.anvil.command.StringReader.ParseT

import scala.math.Ordered.orderingToOrdered

trait ArgumentParser[T] {
  def parse[F[_]: Monad]: ParseT[F, T]
}

trait RangedArgumentParser[T: Ordering] extends ArgumentParser[T] {
  val min: T
  val max: T
}

// TODO: RangedArgumentTypeCompanion (with .all)

object RangedArgumentParser {
  // For now, this uses the same ParseT as in StringReader.
  // TODO: Replace with new validation type
  def validateArgument[F[_]: Monad, T: Ordering](min: T, max: T, parse: ParseT[F, T]): ParseT[F, T] =
    for {
      input <- parse.get
      value <- parse
      result <- ParseT.liftF {
        if (value < min || value > max) {
          // TODO: Select the correct part of the input
          EitherT.leftT(ParseError(input.string, "is out of range"))
        } else {
          EitherT.pure(value)
        }
      }
    } yield result
}

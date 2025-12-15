package org.anvilpowered.anvil.core.kbrig.argument

import cats.Monad
import cats.data.EitherT
import org.anvilpowered.anvil.core.kbrig.StringReader.ParseT

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

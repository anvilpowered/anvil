/*
 *   KBrig - AnvilPowered.org
 *   Copyright (c) 2023 Contributors
 *
 *     Use of this source code is governed by an MIT-style license that can be found
 *     in the LICENSE file or at https://opensource.org/licenses/MIT.
 */
package org.anvilpowered.anvil.core.kbrig.context

import cats.Monad
import cats.data.{EitherT, ReaderT}
import cats.effect.IO
import org.anvilpowered.anvil.core.kbrig.exception.{ArgumentError, CommandError}

import scala.annotation.tailrec
import scala.reflect.ClassTag
import scala.util.Try

case class CommandContext[+S](
    source: S,
    input: String,
    argumentFetcher: ArgumentFetcher,
    child: Option[CommandContext[S]],
    forks: Boolean,
)

object CommandContext {
  trait Scope[+S] {
    val context: CommandContext[S]

    def abort(): IO[String]
  }

  extension [S](context: CommandContext[S]) {
    def fetch[F[_]: Monad, T: ClassTag]: ReaderT[[X] =>> EitherT[F, ArgumentError, X], String, T] = context.argumentFetcher.fetch

    def lastChild: CommandContext[S] = {
      @tailrec def findLast(ctx: CommandContext[S]): CommandContext[S] =
        ctx.child match {
          case None           => ctx
          case Some(childCtx) => findLast(childCtx)
        }

      findLast(context)
    }
  }
}

trait ArgumentFetcher {

  /** Fetch the argument with the name matching the string input.
    */
  def fetch[F[_]: Monad, T: ClassTag]: ReaderT[[X] =>> EitherT[F, ArgumentError, X], String, T]
}

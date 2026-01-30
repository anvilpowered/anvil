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
package org.anvilpowered.anvil.command.context

import cats.Monad
import cats.data.{EitherT, OptionT, ReaderT}
import cats.effect.IO
import org.anvilpowered.anvil.command.exception.{ArgumentError, CommandError}

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
  def fetch[F[_]: Monad, T: ClassTag](name: String): EitherT[F, ArgumentError, T]
}

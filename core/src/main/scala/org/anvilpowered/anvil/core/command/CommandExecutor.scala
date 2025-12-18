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

package org.anvilpowered.anvil.core.command

import cats.effect.IO
import cats.{FlatMap, Monad}
import org.anvilpowered.anvil.core.kbrig.StringReader
import org.anvilpowered.anvil.core.kbrig.StringReader.ParseT
import org.anvilpowered.anvil.core.kbrig.argument.{BooleanArgumentParser, StringArgumentParser, StringArgumentType}

trait CommandExecutor[F[_]: Monad] {
  def execute(
      source: CommandSource,
      command: String,
  ): F[Boolean]

  def executeAsConsole(command: String): F[Boolean]
}

def foo(cmd: CommandExecutor, src: CommandSource): Unit = {
  for {
    _ <- IO.println("Hello")
    b <- cmd.execute(src, "test")
    c <- cmd.execute(src, "test" + b)
    s <- IO.println("World")
  } yield s
}

def bar[F[_]: Monad]: ParseT[F, (Boolean, String, String)] =
  for {
    a <- BooleanArgumentParser.parse
    b <- StringArgumentParser.SingleWord.parse
    c <- StringArgumentParser.GreedyPhrase.parse
  } yield (a, b, c)

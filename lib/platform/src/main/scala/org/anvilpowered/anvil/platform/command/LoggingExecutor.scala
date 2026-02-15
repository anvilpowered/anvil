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

package org.anvilpowered.anvil.platform.command

import cats.effect.IO
import cats.syntax.all.*
import cats.{Applicative, FlatMap, Monad}
import org.typelevel.log4cats.Logger

extension [F[_]: Monad](executor: CommandExecutor[F]) {
  def withLogging(prefix: String = "command")(using logger: Logger[F]): CommandExecutor[F] = {
    def log(success: Boolean, command: String, name: String): F[Unit] =
      if (success) logger.info(s"$name executed $prefix: $command")
      else logger.error(s"$name failed to execute $prefix: $command")

    new CommandExecutor {
      override def execute(source: CommandSource, command: String): F[Boolean] =
        for {
          success <- executor.execute(source, command)
          _ <- log(success, command, source.player.map(_.username.value).getOrElse("<n/a>"))
        } yield success

      override def executeAsConsole(command: String): F[Boolean] =
        for {
          success <- executor.executeAsConsole(command)
          _ <- log(success, command, "Console")
        } yield success
    }
  }
}

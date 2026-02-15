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

import cats.Monad
import cats.data.EitherT
import org.anvilpowered.anvil.chat.Audience
import org.anvilpowered.anvil.command.context.CommandContext
import org.anvilpowered.anvil.command.exception.ArgumentError
import org.anvilpowered.anvil.platform.user.{Player, Subject}

import scala.reflect.ClassTag

case class CommandSource(
    subject: Subject,
    audience: Audience,
    player: Option[Player],
)

object CommandSource {
  extension (context: CommandContext[?]) {
    def extract[F[_]: Monad, T: ClassTag](name: String): EitherT[F, ArgumentError, T] =
      context.argumentFetcher.fetch[F, T](name)
  }
}

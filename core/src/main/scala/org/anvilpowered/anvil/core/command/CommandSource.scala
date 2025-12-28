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

import cats.data.{EitherT, OptionT}
import cats.effect.IO
import cats.{Monad, MonadError}
import net.kyori.adventure.audience.Audience
import org.anvilpowered.anvil.core.PlatformType
import org.anvilpowered.anvil.core.kbrig.context.CommandContext
import org.anvilpowered.anvil.core.kbrig.exception.{ArgumentError, CommandError}
import org.anvilpowered.anvil.core.user.{Player, Subject}

import scala.reflect.ClassTag

trait CommandSource extends PlatformType, Audience, Subject {

  /** The [Player] associated with the executed command, if any.
    */
  val player: Option[Player]
}

object CommandSource {
  extension (context: CommandContext[?]) {
    def extract[F[_]: Monad, T: ClassTag](name: String): EitherT[F, ArgumentError, T] =
      context.argumentFetcher.fetch[F, T](name)
  }
}

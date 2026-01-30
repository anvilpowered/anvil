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

package org.anvilpowered.anvil.platform.user

import cats.effect.Async

trait TransferService {
  def transfer[F[_]: Async](player: Player)(host: String, port: Int): F[Unit]
  def storeCookie[F[_]: Async](player: Player)(key: String, value: Array[Byte]): F[Unit]
}

object TransferService {
  extension (player: Player)(using tr: TransferService) {
    def transfer[F[_]: Async](host: String, port: Int): F[Unit] = tr.transfer(player)(host, port)
    def storeCookie[F[_]: Async](key: String, value: Array[Byte]): F[Unit] = tr.storeCookie(player)(key, value)
  }
}

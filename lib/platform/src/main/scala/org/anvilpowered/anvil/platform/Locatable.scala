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

package org.anvilpowered.anvil.platform

import cats.effect.Async
import cats.data.OptionT

trait Locatable[-T] {
  def isLocal(obj: T): Boolean
  def locate[F[_]: Async](obj: T): OptionT[F, Server]
}

object Locatable {
  extension [T: Locatable as ls](obj: T) {
    def isLocal: Boolean = ls.isLocal(obj)
    def locate[F[_]: Async]: OptionT[F, Server] = ls.locate(obj)
  }
}

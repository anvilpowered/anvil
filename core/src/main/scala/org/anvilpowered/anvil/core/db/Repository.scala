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

package org.anvilpowered.anvil.core.db

import cats.data.ReaderT

import java.util.UUID
import cats.data.OptionT
import cats.effect.Async

trait Repository[E <: DomainEntity] {
  def findById[F[_]: Async](uuid: UUID): OptionT[F, E]

  def exists[F[_]: Async](uuid: UUID): OptionT[F, Boolean]

  def countAll[F[_]: Async]: F[Long]

  def deleteById[F[_]: Async](id: UUID): F[Boolean]
}

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

package org.anvilpowered.anvil.core.config

import cats.{Monad, Monoid}
import cats.data.OptionT
import cats.effect.Sync
import cats.kernel.CommutativeMonoid
import cats.kernel.instances.OptionMonoid
import cats.syntax.all.*

trait Registry {
  def getDefault[T](key: Key[T]): T

  def get[F[_]: Monad, T](key: Key[T]): OptionT[F, T]

  def apply[F[_]: Monad, T](key: Key[T]): F[T] = get(key).getOrElse(getDefault(key))
}

object EmptyRegistry extends Registry {
  override def getDefault[T](key: Key[T]): T = key.fallback
  override def get[F[_]: Sync, T](key: Key[T]): OptionT[F, T] = OptionT.none
}

object RegistryMonoid extends Monoid[Registry] {
  override def empty: Registry = EmptyRegistry
  override def combine(x: Registry, y: Registry): Registry = new Registry {
    override def getDefault[T](key: Key[T]): T = key.monoid.combine(x.getDefault(key), y.getDefault(key))
    override def get[F[_]: Monad, T](key: Key[T]): OptionT[F, T] = {
      OptionT { (x.get(key).value, y.get(key).value).mapN(OptionMonoid[T](using key.monoid).combine) }
    }
  }
}

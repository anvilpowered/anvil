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

import cats.Monoid
import io.circe.Codec
import io.leangen.geantyref.TypeToken

case class Key[T](
    name: String,
    typeToken: TypeToken[T],

    /** The [[Codec]] instance for this [[Key]] defines how values are serialized to and deserialized from a JSON representation.
      */
    codec: Codec[T],

    /** The [[Monoid]] instance for this [[Key]] defines how values are combined if multiple values are present.
      *
      * The combine implementation is expected be left-based (i.e. the left argument should be preferred) if it is not possible to incorporate the values
      * of both arguments.
      */
    monoid: Monoid[T],
    fallback: T,
    description: Option[String],
)

object Key {
  given ordering: Ordering[Key[?]] = Ordering.by(_.name)

  inline def typeTokenOf[T]: TypeToken[T] = new TypeToken[T]() {}
}

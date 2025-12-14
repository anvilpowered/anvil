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

import io.leangen.geantyref.TypeToken

abstract class AbstractKeyBuilder[
    T,
    K <: Key[T],
    AF <: Key.BuilderFacet[T, K, AF],
    NF <: Key.NamedBuilderFacet[T, K, NF],
](
    val typeTok: TypeToken[T],
) extends Key.FacetedBuilder[T, K, AF, NF] {
  var name: Option[String] = None
  var fallback: Option[T] = None
  var description: Option[String] = None

  override def name(name: String): this.type = {
    this.name = Some(name)
    this
  }

  override def fallback(fallback: T): this.type = {
    this.fallback = Some(fallback)
    this
  }

  override def description(description: String): this.type = {
    this.description = Some(description)
    this
  }
}

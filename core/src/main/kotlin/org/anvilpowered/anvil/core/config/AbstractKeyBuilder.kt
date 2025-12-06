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

abstract class AbstractKeyBuilder<
  T : Any, K : Key<T>, B : Key.FacetedBuilder<T, K, B, AF, NF>,
  AF : Key.BuilderFacet<T, K, AF>, NF : Key.NamedBuilderFacet<T, K, NF>,
  >(val type: TypeToken<T>) : Key.FacetedBuilder<T, K, B, AF, NF> {

  var name: String? = null
  var fallback: T? = null
  var description: String? = null

  protected abstract fun self(): B

  override fun name(name: String): B {
    this.name = name
    return self()
  }

  override fun fallback(fallback: T?): B {
    this.fallback = fallback
    return self()
  }

  override fun description(description: String?): B {
    this.description = description
    return self()
  }
}

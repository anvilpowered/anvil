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

/**
 * A [Registry] that always returns the default value for all keys.
 */
object DefaultRegistry : Registry {
  override fun <T : Any> getDefault(key: Key<T>): T = key.fallback

  override fun <E : Any> getDefault(
    key: ListKey<E>,
    index: Int,
  ): E =
    key.fallback.getOrNull(index)
      ?: throw NoSuchElementException("No default value for key ${key.name} at index $index")

  override fun <K : Any, V : Any> getDefault(
    key: MapKey<K, V>,
    mapKey: K,
  ): V =
    key.fallback[mapKey]
      ?: throw NoSuchElementException("No default value for key ${key.name} at key $mapKey")

  override fun <T : Any> getStrict(key: SimpleKey<T>): T? = null

  override fun <E : Any> getStrict(key: ListKey<E>): List<E>? = null

  override fun <E : Any> getStrict(
    key: ListKey<E>,
    index: Int,
  ): E? = null

  override fun <K : Any, V : Any> getStrict(key: MapKey<K, V>): Map<K, V>? = null

  override fun <K : Any, V : Any> getStrict(
    key: MapKey<K, V>,
    mapKey: K,
  ): V? = null
}

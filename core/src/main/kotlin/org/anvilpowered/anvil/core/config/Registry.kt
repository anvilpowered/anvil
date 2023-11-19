/*
 *   Anvil - AnvilPowered.org
 *   Copyright (C) 2019-2023 Contributors
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

interface Registry {
    fun <T : Any> getStrict(key: SimpleKey<T>): T?
    fun <T : Any> getDefault(key: SimpleKey<T>): T
    operator fun <T : Any> get(key: SimpleKey<T>): T = getStrict(key) ?: getDefault(key)

    fun <E : Any> getStrict(key: ListKey<E>): List<E>?
    fun <E : Any> getDefault(key: ListKey<E>): List<E>
    operator fun <E : Any> get(key: ListKey<E>): List<E> = getStrict(key) ?: getDefault(key)

    fun <E : Any> getStrict(key: ListKey<E>, index: Int): E?
    fun <E : Any> getDefault(key: ListKey<E>, index: Int): E
    operator fun <E : Any> get(key: ListKey<E>, index: Int): E = getStrict(key, index) ?: getDefault(key, index)

    fun <K : Any, V : Any> getStrict(key: MapKey<K, V>): Map<K, V>?
    fun <K : Any, V : Any> getDefault(key: MapKey<K, V>): Map<K, V>
    operator fun <K : Any, V : Any> get(key: MapKey<K, V>): Map<K, V> = getStrict(key) ?: getDefault(key)

    fun <K : Any, V : Any> getStrict(key: MapKey<K, V>, mapKey: K): V?
    fun <K : Any, V : Any> getDefault(key: MapKey<K, V>, mapKey: K): V
    operator fun <K : Any, V : Any> get(key: MapKey<K, V>, mapKey: K): V = getStrict(key, mapKey) ?: getDefault(key, mapKey)

    interface Scope {
        val registry: Registry
    }
}

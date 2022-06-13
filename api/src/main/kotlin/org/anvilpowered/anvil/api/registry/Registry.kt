/*
 *   Anvil - Registry
 *   Copyright (C) 2020-2021
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.anvilpowered.anvil.api.registry

import kotlinx.coroutines.flow.SharedFlow
import org.anvilpowered.anvil.api.registry.key.Key

interface Registry : EventSource {

    /**
     * Gets this registry's value for the provided [Key]
     * or null if it is not present.
     *
     * @param <T> The value type of the provided [Key]
     * @param key The [Key] to get the value for
     * @return This registry's value for the provided [Key] or null
     */
    operator fun <T : Any> get(key: Key<T>): T?

    /**
     * Gets this registry's default value for the provided [Key]
     * or the fallback value if it is not present.
     *
     * @param <T> The value type of the provided [Key]
     * @param key The [Key] to get the default value for
     * @return This registry's default value for the provided [Key] or the fallback value
     */
    // TODO: Reconsider if this is the best way to store defaults
    fun <T : Any> getDefault(key: Key<T>): T?

    val keys: List<Key<*>>

    override val flow: SharedFlow<RegistryEvent>
}

/**
 * Gets this registry's value for the provided [Key]
 * or the default value if it is not present.
 *
 * @param <T> The value type of the provided [Key]
 * @param key The [Key] to get the value or (if not present) the default value for
 * @return This registry's value for the provided [Key] or the default value
 */
fun <T : Any> Registry.getOrDefault(key: Key<T>): T = get(key) ?: getDefault(key) ?: key.fallbackValue

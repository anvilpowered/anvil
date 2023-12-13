/*
 *   Anvil - AnvilPowered.org
 *   Copyright (C) 2019-2024 Contributors
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
 * A [Registry] implementation that checks environment variables.
 */
class EnvironmentRegistry(private val prefix: String, private val delegate: Registry? = null) : Registry {

    private val Key<*>.environmentName: String
        get() = prefix + "_" + name

    override fun <T : Any> getDefault(key: Key<T>): T {
        return delegate?.getDefault(key) ?: key.fallback
    }

    override fun <T : Any> getStrict(key: SimpleKey<T>): T? {
        val value = System.getenv(key.environmentName) ?: return delegate?.getStrict(key)
        return key.deserialize(value)
    }

    override fun <E : Any> getStrict(key: ListKey<E>): List<E>? {
        val value = System.getenv(key.environmentName) ?: return delegate?.getStrict(key)
        return key.deserialize(value)
    }

    override fun <E : Any> getStrict(key: ListKey<E>, index: Int): E? {
        val value = System.getenv(key.environmentName) ?: return delegate?.getStrict(key, index)
        return key.deserialize(value)[index]
    }

    override fun <E : Any> getDefault(key: ListKey<E>, index: Int): E {
        return delegate?.getDefault(key, index)
            ?: key.fallback.getOrNull(index)
            ?: throw NoSuchElementException("No default value for key ${key.name} at index $index")
    }

    override fun <K : Any, V : Any> getStrict(key: MapKey<K, V>): Map<K, V>? {
        val value = System.getenv(key.environmentName) ?: return delegate?.getStrict(key)
        return key.deserialize(value)
    }

    override fun <K : Any, V : Any> getStrict(key: MapKey<K, V>, mapKey: K): V? {
        val value = System.getenv(key.environmentName) ?: return delegate?.getStrict(key, mapKey)
        return key.deserialize(value)[mapKey]
    }

    override fun <K : Any, V : Any> getDefault(key: MapKey<K, V>, mapKey: K): V {
        return delegate?.getDefault(key, mapKey)
            ?: key.fallback[mapKey]
            ?: throw NoSuchElementException("No default value for key ${key.name} with mapKey $mapKey")
    }
}

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

/**
 * A [Registry] implementation that checks environment variables.
 */
class EnvironmentRegistry(private val delegate: Registry? = null) : Registry {
    override fun <T : Any> getStrict(key: Key<T>): T? {
        val value = System.getenv(key.name) ?: return delegate?.getStrict(key)
        return key.parse(value)
    }

    override fun <T : Any> getDefault(key: Key<T>): T {
        return delegate?.getDefault(key) ?: throw NoSuchElementException("No default value for key ${key.name}")
    }
}

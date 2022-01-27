/*
 *   Anvil - AnvilPowered
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
package org.anvilpowered.anvil.api.datastore

/**
 * Part of a module
 *
 * @see Manager
 *
 * @see Repository
 */
interface DBComponent<TKey, TDataStore> {
    val tKeyClass: Class<TKey>
    val dataStoreContext: DataStoreContext<TKey, TDataStore>

    /**
     * Tries to convert the given object to [TKey]
     *
     * @param obj To try to parse
     * @return The [TKey] representing this `object`
     * @throws UnsupportedOperationException If not implemented
     * @throws IllegalArgumentException      if object was unsuccessfully parsed
     */
    fun parseUnsafe(obj: Any): TKey {
        throw UnsupportedOperationException()
    }

    /**
     * Tries to convert the given object to [TKey]
     *
     * @param obj To try to parse
     * @return If parsing successful, the [TKey] representing this `object`. If unsuccessful, null
     */
    fun parse(obj: Any): TKey? {
        return null
    }
}

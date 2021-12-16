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
package org.anvilpowered.anvil.base.datastore

import com.google.inject.Inject
import org.anvilpowered.anvil.api.datastore.DBComponent
import org.anvilpowered.anvil.api.datastore.DataStoreContext
import java.util.Optional

abstract class BaseComponent<TKey, TDataStore> : DBComponent<TKey, TDataStore> {

    @Inject
    override lateinit var dataStoreContext: DataStoreContext<TKey, TDataStore>

    override val tKeyClass: Class<TKey>
        get() = dataStoreContext.tKeyClass!!

    override fun parse(obj: Any): TKey? {
        return try {
            parseUnsafe(obj)
        } catch (e: IllegalArgumentException) {
            null
        } catch (e: UnsupportedOperationException) {
            null
        }
    }
}

/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.api.component;

import org.anvilpowered.anvil.api.cache.CacheService;
import org.anvilpowered.anvil.api.datastore.DataStoreContext;
import org.anvilpowered.anvil.api.manager.Manager;
import org.anvilpowered.anvil.api.misc.BindingExtensions;
import org.anvilpowered.anvil.api.repository.Repository;

import java.util.Optional;

/**
 * Part of a module
 *
 * @see Manager
 * @see Repository
 * @see CacheService
 * @see BindingExtensions
 */
public interface Component<
    TKey,
    TDataStore> {

    Class<TKey> getTKeyClass();

    DataStoreContext<TKey, TDataStore> getDataStoreContext();

    /**
     * Tries to convert the given object to {@link TKey}
     *
     * @param object To try to parse
     * @return The {@link TKey} representing this {@code object}
     * @throws UnsupportedOperationException If not implemented
     * @throws IllegalArgumentException      if object was unsuccessfully parsed
     */
    default TKey parseUnsafe(Object object) {
        throw new UnsupportedOperationException();
    }

    /**
     * Tries to convert the given object to {@link TKey}
     *
     * @param object To try to parse
     * @return {@link Optional} containing (if parsing successful) the {@link TKey} representing this {@code object}
     */
    default Optional<TKey> parse(Object object) {
        return Optional.empty();
    }
}

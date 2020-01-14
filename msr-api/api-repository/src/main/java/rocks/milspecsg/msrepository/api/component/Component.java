/*
 *     MSRepository - MilSpecSG
 *     Copyright (C) 2019 Cableguy20
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

package rocks.milspecsg.msrepository.api.component;

import rocks.milspecsg.msrepository.api.misc.BindingExtensions;
import rocks.milspecsg.msrepository.api.cache.CacheService;
import rocks.milspecsg.msrepository.api.manager.Manager;
import rocks.milspecsg.msrepository.api.repository.Repository;
import rocks.milspecsg.msrepository.api.datastore.DataStoreContext;

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
     * @return The {@link TKey} representing this {@param object}
     * @throws UnsupportedOperationException If not implemented
     * @throws IllegalArgumentException if object was unsuccessfully parsed
     */
    default TKey parseUnsafe(Object object) {
        throw new UnsupportedOperationException();
    }

    /**
     * Tries to convert the given object to {@link TKey}
     *
     * @param object To try to parse
     * @return {@link Optional} containing (if parsing successful) the {@link TKey} representing this {@param object}
     */
    default Optional<TKey> parse(Object object) {
        return Optional.empty();
    }
}

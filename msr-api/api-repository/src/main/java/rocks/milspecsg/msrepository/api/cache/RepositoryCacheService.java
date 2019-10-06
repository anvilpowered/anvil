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

package rocks.milspecsg.msrepository.api.cache;

import rocks.milspecsg.msrepository.api.storageservice.TypedDataStorageService;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface RepositoryCacheService<TKey, T extends ObjectWithId<TKey>> extends CacheService<T>, TypedDataStorageService<TKey, T> {

    Optional<T> getOne(TKey id);

    Optional<T> deleteOne(TKey id);

    /**
     * @param fromDB {@link Supplier<List>} that retrieves data from datastore
     * @return A list containing all elements that were successfully retrieved from the datastore and saved to the cache
     */
    Supplier<List<T>> save(Supplier<List<T>> fromDB);

    /**
     * @param fromDB {@link List<T>} retrieved from the datastore
     * @return A list containing all elements that were successfully retrieved from the datastore and saved to the cache
     */
    List<T> save(List<T> fromDB);

    /**
     * @param fromDB {@link T} retrieved from the datastore
     * @return An optional containing the saved element
     */
    Optional<T> save(T fromDB);

    /**
     * @param fromCache {@link Supplier} that retrieves data from cache
     * @param fromDB    {@link Supplier} that retrieves data from datastore
     * @return Attempts to retrieve an item from the cache. If the item is not found, retrieve it from the datastore
     */
    <C extends RepositoryCacheService<TKey, T>> Supplier<Optional<T>> ifNotPresent(Function<C, Optional<T>> fromCache, Supplier<Optional<T>> fromDB);

    /**
     * @param id {@link TKey} to query repository with
     * @return The first item that satisfies {@link T#getId()} == {@param id} from the cache.
     * If no matching item is present in the cache, return the first item that
     * satisfies {@link T#getId()} == {@param id} from the datastore
     */
    Supplier<Optional<T>> ifNotPresent(TKey id, Supplier<Optional<T>> fromDB);

}

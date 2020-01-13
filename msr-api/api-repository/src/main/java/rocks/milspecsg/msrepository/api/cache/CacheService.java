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

import rocks.milspecsg.msrepository.api.component.Component;
import rocks.milspecsg.msrepository.api.storageservice.StorageService;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public interface CacheService<
    TKey,
    T extends ObjectWithId<TKey>,
    TDataStore>
    extends StorageService<TKey, T, TDataStore>,
    Component<TKey, TDataStore> {

    /**
     * Starts cache invalidation task
     */
    void startCacheInvalidationTask(Integer intervalSeconds);

    /**
     * Stop cache invalidation task
     */
    void stopCacheInvalidationTask();

    /**
     *
     * @return Cache invalidation task
     */
    Runnable getCacheInvalidationTask();

    /**
     * @return A set containing all parties in the cache
     */
    Set<T> getAll();

    /**
     * Deletes a {@link T} from the cache
     *
     * @param predicate of {@link T} to remove from cache
     * @return An optional containing the {@link T} if it was successfully removed
     */
    Optional<T> deleteOne(Predicate<? super T> predicate);

    /**
     * Deletes a {@link T} from the cache
     *
     * @param t {@link T} to remove from cache
     * @return An optional containing the {@link T} if it was successfully removed
     */
    Optional<T> delete(T t);

    /**
     * Deletes a {@link T} from the cache
     *
     * @param predicate of {@link T} to remove from cache
     * @return A list of successfully deleted elements
     */
    List<T> delete(Predicate<? super T> predicate);

    List<T> getAll(Predicate<? super T> predicate);

    Optional<T> getOne(Predicate<? super T> predicate);

}

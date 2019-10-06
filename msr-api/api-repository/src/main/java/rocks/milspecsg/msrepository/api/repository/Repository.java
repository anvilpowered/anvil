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

package rocks.milspecsg.msrepository.api.repository;

import rocks.milspecsg.msrepository.api.cache.RepositoryCacheService;
import rocks.milspecsg.msrepository.api.storageservice.TypedDataStorageService;
import rocks.milspecsg.msrepository.api.storageservice.TypedStorageService;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Repository<TKey, T extends ObjectWithId<TKey>, C extends RepositoryCacheService<TKey, T>> extends TypedDataStorageService<TKey, T> {

    /**
     * @return An empty {@link T}
     */
    T generateEmpty();

    /**
     * @param item Object to insert
     * @return The inserted item with {@link T#getId()} ()} set
     */
    CompletableFuture<Optional<T>> insertOneIntoDS(T item);

    /**
     * @param item Object to insert
     * @return The inserted item with {@link T#getId()} ()} set
     */
    CompletableFuture<Optional<T>> insertOne(T item);

    /**
     * @param id {@link TKey} to query repository with
     * @return The first item that satisfies {@link T#getId()} == {@param id}
     */
    CompletableFuture<Optional<T>> getOneFromDS(TKey id);

    /**
     * @param id {@link TKey} to query repository with
     * @return The first item that satisfies {@link T#getId()} == {@param id}
     */
    CompletableFuture<Optional<T>> getOne(TKey id);

    /**
     * @return A list of all {@link TKey} ids in the repository
     */
    CompletableFuture<List<TKey>> getAllIds();

    /**
     * Deletes the first item that satisfies {@link T#getId()} == {@param id}
     *
     * @param id {@link TKey} to query repository with
     * @return Whether or not an item was found and deleted
     */
    CompletableFuture<Boolean> deleteOneFromDS(TKey id);

    /**
     * Deletes the first item that satisfies {@link T#getId()} == {@param id}
     *
     * @param id {@link TKey} to query repository with
     * @return Whether or not an item was found and deleted
     */
    CompletableFuture<Boolean> deleteOne(TKey id);

    default Optional<C> getRepositoryCacheService() {
        return Optional.empty();
    }

    CompletableFuture<Optional<T>> ifNotPresent(Function<C, Optional<T>> fromCache, Supplier<Optional<T>> fromDB);

}
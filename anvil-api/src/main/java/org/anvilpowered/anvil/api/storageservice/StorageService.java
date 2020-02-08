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

package org.anvilpowered.anvil.api.storageservice;

import org.anvilpowered.anvil.api.component.Component;
import org.anvilpowered.anvil.api.model.ObjectWithId;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface StorageService<
    TKey,
    T extends ObjectWithId<TKey>,
    TDataStore>
    extends Component<TKey, TDataStore> {

    /**
     * @return An empty {@link T}
     */
    T generateEmpty();

    Class<T> getTClass();

    /**
     * @param item Object to insert
     * @return The inserted item
     */
    CompletableFuture<Optional<T>> insertOne(T item);

    /**
     * @param list {@link List<T>} to insert
     * @return {@link CompletableFuture} of {@link List<T>} containing all {@link T} that were successfully inserted
     */
    CompletableFuture<List<T>> insert(List<T> list);

    /**
     * @return A list of all {@link TKey} ids in the repository
     */
    CompletableFuture<List<TKey>> getAllIds();

    /**
     * @param id {@link TKey} to query repository with
     * @return The first item that satisfies {@link T#getId()} == {@param id}
     */
    CompletableFuture<Optional<T>> getOne(TKey id);

    /**
     * @param id {@link Object} to query repository with. Will attempt to parse to {@link TKey}
     * @return The first item that satisfies {@link T#getId()} == {@param id}
     */
    CompletableFuture<Optional<T>> parseAndGetOne(Object id);

    /**
     * Deletes the first item that satisfies {@link T#getId()} == {@param id}
     *
     * @param id {@link TKey} to query repository with
     * @return Whether or not an item was found and deleted
     */
    CompletableFuture<Boolean> deleteOne(TKey id);

    /**
     * Deletes the first item that satisfies {@link T#getId()} == {@param id}
     *
     * @param id {@link Object} to query repository with. Will attempt to parse to {@link TKey}
     * @return Whether or not an item was found and deleted
     */
    CompletableFuture<Boolean> parseAndDeleteOne(Object id);
}

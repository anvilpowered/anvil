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
import org.anvilpowered.anvil.api.util.TimeFormatService;

import java.time.Instant;
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
     * @param item {@link T document} to insert
     * @return An {@link Optional} containing the inserted {@link T document}
     * if successful, otherwise {@link Optional#empty()}
     */
    CompletableFuture<Optional<T>> insertOne(T item);

    /**
     * @param list {@link List} of {@link T documents} to insert
     * @return {@link List} of all {@link T documents} that were successfully inserted
     */
    CompletableFuture<List<T>> insert(List<T> list);

    /**
     * @return A {@link List} of all {@link TKey ids} in the repository
     */
    CompletableFuture<List<TKey>> getAllIds();

    /**
     * @return A {@link List} of all {@link T documents} in the repository
     */
    CompletableFuture<List<T>> getAll();

    /**
     * Attempts to find a matching {@link T document} with
     * the provided {@link TKey id}
     *
     * @param id {@link TKey} to query repository with
     * @return An {@link Optional} containing a matching {@link T document}
     * if successful, otherwise {@link Optional#empty()}
     */
    CompletableFuture<Optional<T>> getOne(TKey id);

    /**
     * Attempts to find the first {@link T document} where
     * {@link Instant#getEpochSecond()} retrieved from
     * {@link ObjectWithId#getCreatedUtc()} is equal to
     * {@link Instant#getEpochSecond()} of the provided
     * {@link Instant}
     *
     * @param createdUtc {@link Instant} to query repository with
     * @return An {@link Optional} containing  if successful,
     * otherwise {@link Optional#empty()}
     */
    CompletableFuture<Optional<T>> getOne(Instant createdUtc);

    /**
     * Attempts to find a matching {@link T document} by
     * parsing provided {@link Object}.
     *
     * <p>
     * Will first attempt to parse the provided {@link Object}
     * as an id. If parsing is successful, will return the
     * result of {@link #getOne(Object)}.
     * </p>
     *
     * <p>
     * If parsing as an id is unsuccessful, will attempt to parse
     * the provided {@link Object} as createdUtc. If parsing is successful,
     * will return the result of {@link #getOne(Instant)}
     * </p>
     *
     * <p>
     * Note: if id parsing is successful but no document is found,
     * will not attempt to parse as time
     * </p>
     *
     * @param idOrTime {@link Object} to parse. Can be an actual instance of
     *                 {@link TKey} or a {@link String} representation. Can
     *                 also be wrapped in an {@link Optional}
     * @return An {@link Optional} containing a matching {@link T document}
     * if successful, otherwise {@link Optional#empty()}
     * @see Component#parseUnsafe(Object)
     * @see Component#parse(Object)
     * @see TimeFormatService#parseInstantUnsafe(String)
     * @see TimeFormatService#parseInstant(String)
     */
    CompletableFuture<Optional<T>> parseAndGetOne(Object idOrTime);

    /**
     * Attempts to delete a matching {@link T document} with
     * the provided {@link TKey id}
     *
     * @param id {@link TKey} to query repository with
     * @return Whether or not an item was found and deleted
     */
    CompletableFuture<Boolean> deleteOne(TKey id);

    /**
     * Attempts to delete the first {@link T document} where
     * {@link Instant#getEpochSecond()} retrieved from
     * {@link ObjectWithId#getCreatedUtc()} is equal to
     * {@link Instant#getEpochSecond()} of the provided
     * {@link Instant}
     *
     * @param createdUtc {@link Instant} to query repository with
     * @return Whether a {@link T document} was found and deleted
     */
    CompletableFuture<Boolean> deleteOne(Instant createdUtc);

    /**
     * Attempts to delete a matching {@link T document} by
     * parsing provided {@link Object}.
     *
     * <p>
     * Will first attempt to parse the provided {@link Object}
     * as an id. If parsing is successful, will return the
     * result of {@link #deleteOne(Object)}.
     * </p>
     *
     * <p>
     * If parsing as an id is unsuccessful, will attempt to parse
     * the provided {@link Object} as createdUtc. If parsing is successful,
     * will return the result of {@link #deleteOne(Instant)}
     * </p>
     *
     * <p>
     * Note: if id parsing is successful but no document is found,
     * will not attempt to parse as time
     * </p>
     *
     * @param idOrTime {@link Object} to parse. Can be an actual instance of
     *                 {@link TKey} or a {@link String} representation. Can
     *                 also be wrapped in an {@link Optional}
     * @return Whether a {@link T document} was found and deleted
     * @see Component#parseUnsafe(Object)
     * @see Component#parse(Object)
     * @see TimeFormatService#parseInstantUnsafe(String)
     * @see TimeFormatService#parseInstant(String)
     */
    CompletableFuture<Boolean> parseAndDeleteOne(Object idOrTime);
}

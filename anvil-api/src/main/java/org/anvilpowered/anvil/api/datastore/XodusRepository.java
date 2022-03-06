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

package org.anvilpowered.anvil.api.datastore;

import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import jetbrains.exodus.entitystore.StoreTransaction;
import org.anvilpowered.anvil.api.model.ObjectWithId;

import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public interface XodusRepository<
    T extends ObjectWithId<EntityId>>
    extends Repository<EntityId, T, PersistentEntityStore> {

    Iterator<T> iterator(
        Function<? super StoreTransaction, ? extends Iterable<Entity>> query);

    CompletableFuture<List<T>> getAll(
        Function<? super StoreTransaction, ? extends Iterable<Entity>> query);

    CompletableFuture<Optional<T>> getOne(
        Function<? super StoreTransaction, ? extends Iterable<Entity>> query);

    CompletableFuture<Boolean> delete(
        Function<? super StoreTransaction, ? extends Iterable<Entity>> query);

    CompletableFuture<Boolean> update(
        Function<? super StoreTransaction, ? extends Iterable<Entity>> query,
        Consumer<? super Entity> update);

    CompletableFuture<Boolean> update(
        Optional<Function<? super StoreTransaction, ? extends Iterable<Entity>>> optionalQuery,
        Consumer<? super Entity> update);

    Function<? super StoreTransaction, ? extends Iterable<Entity>> asQuery(
        EntityId id);

    Function<? super StoreTransaction, ? extends Iterable<Entity>> asQuery(
        Instant createdUtc);

    Optional<Function<? super StoreTransaction, ? extends Iterable<Entity>>> asQueryForIdOrTime(
        String idOrTime);
}

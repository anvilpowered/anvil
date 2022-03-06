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

package org.anvilpowered.anvil.base.datastore;

import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import jetbrains.exodus.entitystore.StoreTransaction;
import org.anvilpowered.anvil.api.datastore.CacheService;
import org.anvilpowered.anvil.api.datastore.CachedRepository;
import org.anvilpowered.anvil.api.datastore.Repository;
import org.anvilpowered.anvil.api.model.ObjectWithId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Deprecated // will probably break in 0.4
public interface BaseXodusCachedRepository<
    T extends ObjectWithId<EntityId>,
    C extends CacheService<EntityId, T, PersistentEntityStore>>
    extends BaseXodusRepository<T>, CachedRepository<EntityId, T, C, PersistentEntityStore> {

    @Override
    default CompletableFuture<Optional<T>> insertOne(T item) {
        return applyFromDBToCacheConditionally(() -> BaseXodusRepository.super.insertOne(item).join(), Repository::insertOne);
    }

    @Override
    default CompletableFuture<List<T>> insert(List<T> list) {
        return applyFromDBToCache(() -> BaseXodusRepository.super.insert(list).join(), Repository::insert);
    }

    @Override
    default CompletableFuture<Optional<T>> getOne(EntityId id) {
        return applyToBothConditionally(c -> c.getOne(id).join(), () -> BaseXodusRepository.super.getOne(id).join());
    }

    @Override
    default CompletableFuture<Boolean> delete(Function<? super StoreTransaction, ? extends Iterable<Entity>> query) {
        return applyFromDBToCache(() ->
            getDataStoreContext().getDataStore().computeInTransaction(txn -> {
                List<EntityId> toDelete = new ArrayList<>();
                query.apply(txn).forEach(entity -> {
                    toDelete.add(entity.getId());
                    entity.delete();
                });
                return txn.commit() ? toDelete : Collections.<EntityId>emptyList();
            }), (c, toDelete) -> toDelete.forEach(id -> c.deleteOne(id).join()))
            .thenApplyAsync(result -> !result.isEmpty());
    }

    @Override
    default CompletableFuture<Boolean> deleteOne(EntityId id) {
        return applyFromDBToCache(() -> BaseXodusRepository.super.deleteOne(id).join(), (c, b) -> {
            if (b) {
                c.deleteOne(id).join();
            }
        });
    }
}

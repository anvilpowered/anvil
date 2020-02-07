/*
 *   Anvil - MilSpecSG
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

package rocks.milspecsg.anvil.common.repository;

import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import jetbrains.exodus.entitystore.StoreTransaction;
import rocks.milspecsg.anvil.api.cache.CacheService;
import rocks.milspecsg.anvil.api.model.ObjectWithId;
import rocks.milspecsg.anvil.api.repository.CachedRepository;
import rocks.milspecsg.anvil.api.storageservice.StorageService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface CommonXodusCachedRepository<
    T extends ObjectWithId<EntityId>,
    C extends CacheService<EntityId, T, PersistentEntityStore>>
    extends CommonXodusRepository<T>, CachedRepository<EntityId, T, C, PersistentEntityStore> {

    @Override
    default CompletableFuture<Optional<T>> insertOne(T item) {
        return applyFromDBToCacheConditionally(() -> CommonXodusRepository.super.insertOne(item).join(), StorageService::insertOne);
    }

    @Override
    default CompletableFuture<List<T>> insert(List<T> list) {
        return applyFromDBToCache(() -> CommonXodusRepository.super.insert(list).join(), StorageService::insert);
    }

    @Override
    default CompletableFuture<Optional<T>> getOne(EntityId id) {
        return applyToBothConditionally(c -> c.getOne(id).join(), () -> CommonXodusRepository.super.getOne(id).join());
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
        return applyFromDBToCache(() -> CommonXodusRepository.super.deleteOne(id).join(), (c, b) -> {
            if (b) {
                c.deleteOne(id).join();
            }
        });
    }
}

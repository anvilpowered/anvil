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

package rocks.milspecsg.msrepository.service.common.repository;

import jetbrains.exodus.entitystore.*;
import rocks.milspecsg.msrepository.api.cache.CacheService;
import rocks.milspecsg.msrepository.api.repository.XodusRepository;
import rocks.milspecsg.msrepository.api.storageservice.StorageService;
import rocks.milspecsg.msrepository.datastore.xodus.XodusConfig;
import rocks.milspecsg.msrepository.model.data.dbo.Mappable;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;
import rocks.milspecsg.msrepository.service.common.component.CommonXodusComponent;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public interface CommonXodusRepository<
    T extends ObjectWithId<EntityId> & Mappable<Entity>,
    C extends CacheService<EntityId, T, PersistentEntityStore, XodusConfig>>
    extends XodusRepository<T, C>, CommonXodusComponent {

    @Override
    default CompletableFuture<Optional<T>> insertOne(T item) {
        return applyFromDBToCacheConditionally(() ->
            getDataStoreContext().getDataStore().map(dataStore -> {
                dataStore.executeInTransaction(txn -> {
                    final Entity entity = txn.newEntity(getTClass().getSimpleName());
                    item.writeTo(entity);
                    item.setId(entity.getId());
                    txn.commit();
                });
                return item;
            }), StorageService::insertOne);
    }

    @Override
    default CompletableFuture<List<T>> insert(List<T> list) {
        return applyFromDBToCache(() ->
            getDataStoreContext().getDataStore().map(dataStore -> {
                dataStore.executeInTransaction(txn -> {
                    list.forEach(item -> {
                        final Entity entity = txn.newEntity(getTClass().getSimpleName());
                        item.writeTo(entity);
                        item.setId(entity.getId());
                    });
                    txn.commit();
                });
                return list;
            }).orElse(Collections.emptyList()), StorageService::insert);
    }

    @Override
    default CompletableFuture<List<T>> getAll(Function<? super StoreTransaction, ? extends Iterable<Entity>> query) {
        return CompletableFuture.supplyAsync(() -> getDataStoreContext().getDataStore().map(dataStore ->
            dataStore.computeInReadonlyTransaction(txn -> {
                return StreamSupport.stream(query.apply(txn).spliterator(), false).map(e -> {
                    T item = generateEmpty();
                    item.readFrom(e);
                    return item;
                }).collect(Collectors.toList());
            })).orElse(Collections.emptyList()));
    }

    @Override
    default CompletableFuture<Optional<T>> getOne(Function<? super StoreTransaction, ? extends Iterable<Entity>> query) {
        return CompletableFuture.supplyAsync(() -> getDataStoreContext().getDataStore().flatMap(dataStore ->
            dataStore.computeInReadonlyTransaction(txn -> {
                Iterator<Entity> iterator = query.apply(txn).iterator();
                if (iterator.hasNext()) {
                    T item = generateEmpty();
                    item.readFrom(iterator.next());
                    return Optional.of(item);
                } else {
                    return Optional.empty();
                }
            })));
    }

    @Override
    default CompletableFuture<Optional<T>> getOne(EntityId id) {
        return applyToBothConditionally(c -> c.getOne(id).join(),
            () -> getDataStoreContext().getDataStore().flatMap(dataStore ->
                dataStore.computeInReadonlyTransaction(txn -> {
                    Entity entity;
                    try {
                        entity = txn.getEntity(id);
                    } catch (EntityRemovedInDatabaseException ignored) {
                        return Optional.empty();
                    }
                    T item = generateEmpty();
                    item.readFrom(entity);
                    return Optional.of(item);
                })));
    }

    @Override
    default CompletableFuture<List<EntityId>> getAllIds() {
        return CompletableFuture.supplyAsync(() ->
            getDataStoreContext().getDataStore().map(dataStore ->
                dataStore.computeInReadonlyTransaction(txn -> {
                    final EntityIterable iterable = txn.getAll(getTClass().getSimpleName());
                    final EntityIterator iterator = iterable.iterator();
                    final long roughCount = iterable.getRoughCount();
                    final int buffer = 50;
                    int listSize = roughCount > Integer.MAX_VALUE - buffer
                        ? Integer.MAX_VALUE - buffer // woah
                        : (int) (roughCount + buffer);
                    final List<EntityId> list = new ArrayList<>(listSize);
                    while (iterator.hasNext()) {
                        list.add(iterator.nextId());
                    }
                    if (iterator.shouldBeDisposed()) {
                        iterator.dispose();
                    }
                    return list;
                })).orElse(Collections.emptyList()));
    }

    @Override
    default CompletableFuture<Boolean> delete(Function<? super StoreTransaction, ? extends Iterable<Entity>> query) {
        return applyFromDBToCacheConditionally(() ->
            getDataStoreContext().getDataStore().map(dataStore ->
                dataStore.computeInTransaction(txn -> {
                    List<EntityId> toDelete = new ArrayList<>();
                    query.apply(txn).forEach(entity -> {
                        toDelete.add(entity.getId());
                        entity.delete();
                    });
                    return txn.commit() ? toDelete : Collections.<EntityId>emptyList();
                })
            ), (c, toDelete) -> toDelete.forEach(id -> c.deleteOne(id).join()))
            .thenApplyAsync(result -> result.filter(list -> !list.isEmpty()).isPresent());
    }

    @Override
    default CompletableFuture<Boolean> deleteOne(EntityId id) {
        return applyFromDBToCache(() ->
            getDataStoreContext().getDataStore().map(dataStore ->
                dataStore.computeInTransaction(txn -> {
                    txn.getEntity(id).delete();
                    return txn.commit();
                })
            ).orElse(false), (c, b) -> {
            if (b) {
                c.deleteOne(id).join();
            }
        });
    }

    @Override
    default Function<? super StoreTransaction, ? extends Iterable<Entity>> asQuery(EntityId id) {
        return txn -> Collections.singletonList(txn.getEntity(id));
    }
}

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

package rocks.milspecsg.msrepository.common.repository;

import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.entitystore.EntityIterable;
import jetbrains.exodus.entitystore.EntityIterator;
import jetbrains.exodus.entitystore.EntityRemovedInDatabaseException;
import jetbrains.exodus.entitystore.StoreTransaction;
import rocks.milspecsg.msrepository.api.model.Mappable;
import rocks.milspecsg.msrepository.api.model.ObjectWithId;
import rocks.milspecsg.msrepository.api.repository.XodusRepository;
import rocks.milspecsg.msrepository.common.component.CommonXodusComponent;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public interface CommonXodusRepository<
    T extends ObjectWithId<EntityId>>
    extends XodusRepository<T>, CommonXodusComponent {

    @Override
    default CompletableFuture<Optional<T>> insertOne(T item) {
        return CompletableFuture.supplyAsync(() ->
            getDataStoreContext().getDataStore().computeInTransaction(txn -> {
                final Entity entity = txn.newEntity(getTClass().getSimpleName());
                ((Mappable<Entity>) item).writeTo(entity);
                item.setId(entity.getId());
                return txn.commit() ? Optional.of(item) : Optional.empty();
            })
        );
    }

    @Override
    default CompletableFuture<List<T>> insert(List<T> list) {
        return CompletableFuture.supplyAsync(() ->
            getDataStoreContext().getDataStore().computeInTransaction(txn -> {
                list.forEach(item -> {
                    final Entity entity = txn.newEntity(getTClass().getSimpleName());
                    ((Mappable<Entity>) item).writeTo(entity);
                    item.setId(entity.getId());
                });
                txn.commit();
                return list;
            })
        );
    }

    @Override
    default CompletableFuture<List<T>> getAll(Function<? super StoreTransaction, ? extends Iterable<Entity>> query) {
        return CompletableFuture.supplyAsync(() ->
            getDataStoreContext().getDataStore().computeInReadonlyTransaction(txn ->
                StreamSupport.stream(query.apply(txn).spliterator(), false).map(e -> {
                    T item = generateEmpty();
                    ((Mappable<Entity>) item).readFrom(e);
                    return item;
                }).collect(Collectors.toList()))
        );
    }

    @Override
    default CompletableFuture<Optional<T>> getOne(Function<? super StoreTransaction, ? extends Iterable<Entity>> query) {
        return CompletableFuture.supplyAsync(() ->
            getDataStoreContext().getDataStore().computeInReadonlyTransaction(txn -> {
                Iterator<Entity> iterator = query.apply(txn).iterator();
                if (iterator.hasNext()) {
                    T item = generateEmpty();
                    ((Mappable<Entity>) item).readFrom(iterator.next());
                    return Optional.of(item);
                } else {
                    return Optional.empty();
                }
            })
        );
    }

    @Override
    default CompletableFuture<Optional<T>> getOne(EntityId id) {
        return CompletableFuture.supplyAsync(() ->
            getDataStoreContext().getDataStore().computeInReadonlyTransaction(txn -> {
                Entity entity;
                try {
                    entity = txn.getEntity(id);
                } catch (EntityRemovedInDatabaseException ignored) {
                    return Optional.empty();
                }
                T item = generateEmpty();
                ((Mappable<Entity>) item).readFrom(entity);
                return Optional.of(item);
            })
        );
    }

    @Override
    default CompletableFuture<List<EntityId>> getAllIds() {
        return CompletableFuture.supplyAsync(() ->
            getDataStoreContext().getDataStore().computeInReadonlyTransaction(txn -> {
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
            })
        );
    }

    @Override
    default CompletableFuture<Boolean> delete(Function<? super StoreTransaction, ? extends Iterable<Entity>> query) {
        return CompletableFuture.supplyAsync(() ->
            getDataStoreContext().getDataStore().computeInTransaction(txn -> {
                query.apply(txn).forEach(Entity::delete);
                return txn.commit();
            })
        );
    }

    @Override
    default CompletableFuture<Boolean> deleteOne(EntityId id) {
        return CompletableFuture.supplyAsync(() ->
            getDataStoreContext().getDataStore().computeInTransaction(txn -> {
                txn.getEntity(id).delete();
                return txn.commit();
            })
        );
    }

    @Override
    default CompletableFuture<Boolean> update(Function<? super StoreTransaction, ? extends Iterable<Entity>> query, Consumer<? super Entity> update) {
        return CompletableFuture.supplyAsync(() ->
            getDataStoreContext().getDataStore().computeInTransaction(txn -> {
                query.apply(txn).forEach(e -> {
                    update.accept(e);
                    Instant now = OffsetDateTime.now(ZoneOffset.UTC).toInstant();
                    e.setProperty("updatedUtcSeconds", now.getEpochSecond());
                    e.setProperty("updatedUtcNanos", now.getNano());
                });
                return txn.commit();
            })
        );
    }

    @Override
    default Function<? super StoreTransaction, ? extends Iterable<Entity>> asQuery(EntityId id) {
        return txn -> Collections.singletonList(txn.getEntity(id));
    }
}

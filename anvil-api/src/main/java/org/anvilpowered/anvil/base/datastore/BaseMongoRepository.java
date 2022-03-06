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

import com.google.common.collect.ImmutableList;
import dev.morphia.query.Query;
import dev.morphia.query.UpdateOperations;
import kotlin.collections.CollectionsKt;
import kotlin.sequences.SequencesKt;
import org.anvilpowered.anvil.api.Anvil;
import org.anvilpowered.anvil.api.datastore.MongoRepository;
import org.anvilpowered.anvil.api.model.ObjectWithId;
import org.anvilpowered.anvil.api.util.TimeFormatService;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public interface BaseMongoRepository<
    T extends ObjectWithId<ObjectId>>
    extends MongoRepository<T>, BaseMongoComponent {

    @Override
    default CompletableFuture<Optional<Instant>> getCreatedUtc(ObjectId id) {
        return CompletableFuture.completedFuture(
            Optional.of(Instant.ofEpochSecond(id.getTimestamp())));
    }

    @Override
    default CompletableFuture<Optional<T>> insertOne(T item) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                getDataStoreContext().getDataStore().save(item);
            } catch (RuntimeException e) {
                e.printStackTrace();
                return Optional.empty();
            }
            return Optional.of(item);
        });
    }

    @Override
    default CompletableFuture<List<T>> insert(List<T> list) {
        return CompletableFuture.supplyAsync(() ->
            list.stream().filter(item -> {
                try {
                    getDataStoreContext().getDataStore().save(item);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }).collect(Collectors.toList())
        );
    }

    @Override
    default CompletableFuture<Optional<T>> getOne(Query<T> query) {
        return CompletableFuture.supplyAsync(() -> Optional.ofNullable(query.first()));
    }

    @Override
    default CompletableFuture<Optional<T>> getOne(ObjectId id) {
        return getOne(asQuery(id));
    }

    @Override
    default CompletableFuture<Optional<T>> getOne(Instant createdUtc) {
        return getOne(asQuery(createdUtc));
    }

    @Override
    default CompletableFuture<List<ObjectId>> getAllIds() {
        return CompletableFuture.supplyAsync(() ->
            ImmutableList.copyOf(
                SequencesKt.map(CollectionsKt.asSequence(asQuery().project("_id", true)),
                    ObjectWithId::getId).iterator())
        );
    }

    @Override
    default CompletableFuture<Boolean> delete(Query<T> query) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getDataStoreContext().getDataStore().delete(query).getN() > 0;
            } catch (RuntimeException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    @Override
    default CompletableFuture<Boolean> deleteOne(ObjectId id) {
        return delete(asQuery(id));
    }

    @Override
    default CompletableFuture<Boolean> deleteOne(Instant createdUtc) {
        return delete(asQuery(createdUtc));
    }

    @Override
    default UpdateOperations<T> createUpdateOperations() {
        return getDataStoreContext().getDataStore().createUpdateOperations(getTClass());
    }

    @Override
    default UpdateOperations<T> inc(String field, Number value) {
        return createUpdateOperations().inc(field, value);
    }

    @Override
    default UpdateOperations<T> inc(String field) {
        return inc(field, 1);
    }

    @Override
    default UpdateOperations<T> set(String field, Object value) {
        return createUpdateOperations().set(field, value);
    }

    @Override
    default UpdateOperations<T> unSet(String field) {
        return createUpdateOperations().unset(field);
    }

    @Override
    default CompletableFuture<Boolean> update(Query<T> query,
                                              UpdateOperations<T> updateOperations) {
        return CompletableFuture.supplyAsync(() ->
            getDataStoreContext().getDataStore()
                .update(query, updateOperations).getUpdatedCount() > 0);
    }

    @Override
    default CompletableFuture<Boolean> update(Optional<Query<T>> optionalQuery,
                                              UpdateOperations<T> updateOperations) {
        return optionalQuery.map(q -> update(q, updateOperations))
            .orElse(CompletableFuture.completedFuture(false));
    }

    @Override
    default CompletableFuture<List<T>> getAll() {
        return getAll(asQuery());
    }

    @Override
    default CompletableFuture<List<T>> getAll(Query<T> query) {
        return CompletableFuture.supplyAsync(query::asList);
    }

    @Override
    default Query<T> asQuery() {
        return getDataStoreContext().getDataStore().createQuery(getTClass());
    }

    @Override
    default Query<T> asQuery(ObjectId id) {
        return asQuery().field("_id").equal(id);
    }

    @Override
    default Query<T> asQuery(Instant createdUtc) {
        String time = Integer.toHexString((int) createdUtc.getEpochSecond());
        return asQuery()
            .field("_id").greaterThan(new ObjectId(time + "0000000000000000"))
            .field("_id").lessThan(new ObjectId(time + "ffffffffffffffff"));
    }

    @Override
    default Optional<Query<T>> asQueryForIdOrTime(String idOrTime) {
        return parse(idOrTime).map(id -> Optional.of(asQuery(id)))
            .orElseGet(() -> Anvil.getEnvironmentManager().getCoreEnvironment()
                .getInjector().getInstance(TimeFormatService.class).parseInstant(idOrTime)
                .map(this::asQuery));
    }
}

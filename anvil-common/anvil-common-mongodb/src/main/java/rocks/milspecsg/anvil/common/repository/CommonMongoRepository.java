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

import com.mongodb.WriteResult;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import rocks.milspecsg.anvil.api.model.ObjectWithId;
import rocks.milspecsg.anvil.api.repository.MongoRepository;
import rocks.milspecsg.anvil.common.component.CommonMongoComponent;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public interface CommonMongoRepository<
    T extends ObjectWithId<ObjectId>>
    extends MongoRepository<T>, CommonMongoComponent {

    @Override
    default CompletableFuture<Optional<Instant>> getCreatedUtc(ObjectId id) {
        return CompletableFuture.completedFuture(Optional.of(Instant.ofEpochSecond(id.getTimestamp())));
    }

    @Override
    default CompletableFuture<Optional<T>> insertOne(T item) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                item.setId((ObjectId) getDataStoreContext().getDataStore().save(item).getId());
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
                    item.setId((ObjectId) getDataStoreContext().getDataStore().save(item).getId());
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }).collect(Collectors.toList())
        );
    }

    @Override
    default CompletableFuture<Optional<T>> getOne(ObjectId id) {
        return CompletableFuture.supplyAsync(() -> Optional.ofNullable(asQuery(id).get()));
    }

    @Override
    default CompletableFuture<List<ObjectId>> getAllIds() {
        return CompletableFuture.supplyAsync(() ->
            asQuery().project("_id", true)
                .asList().stream().map(ObjectWithId::getId)
                .collect(Collectors.toList())
        );
    }

    @Override
    default CompletableFuture<WriteResult> delete(Query<T> query) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getDataStoreContext().getDataStore().delete(query);
            } catch (RuntimeException e) {
                e.printStackTrace();
                return WriteResult.unacknowledged();
            }
        });
    }

    @Override
    default CompletableFuture<Boolean> deleteOne(ObjectId id) {
        return delete(asQuery(id)).thenApplyAsync(wr -> wr.wasAcknowledged() && wr.getN() > 0);
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
    default CompletableFuture<Boolean> update(Query<T> query, UpdateOperations<T> updateOperations) {
        return CompletableFuture.supplyAsync(() -> getDataStoreContext().getDataStore().update(query, updateOperations).getUpdatedCount() > 0);
    }

    @Override
    default Query<T> asQuery() {
        return getDataStoreContext().getDataStore().createQuery(getTClass());
    }

    @Override
    default Query<T> asQuery(ObjectId id) {
        return asQuery().field("_id").equal(id);
    }
}

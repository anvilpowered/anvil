/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020
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

import org.anvilpowered.anvil.api.model.ObjectWithId;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface MongoRepository<
    T extends ObjectWithId<ObjectId>>
    extends Repository<ObjectId, T, Datastore> {

    CompletableFuture<Optional<T>> getOne(Query<T> query);

    CompletableFuture<List<T>> getAll(Query<T> query);

    CompletableFuture<Boolean> delete(Query<T> query);

    UpdateOperations<T> createUpdateOperations();

    UpdateOperations<T> inc(String field, Number value);

    UpdateOperations<T> inc(String field);

    UpdateOperations<T> set(String field, Object value);

    UpdateOperations<T> unSet(String field);

    CompletableFuture<Boolean> update(Query<T> query, UpdateOperations<T> updateOperations);

    CompletableFuture<Boolean> update(Optional<Query<T>> optionalQuery,
                                      UpdateOperations<T> updateOperations);

    Query<T> asQuery();

    Query<T> asQuery(ObjectId id);

    Query<T> asQuery(Instant createdUtc);

    Optional<Query<T>> asQueryForIdOrTime(String idOrTime);
}

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

package rocks.milspecsg.msrepository.api.repository;

import com.mongodb.WriteResult;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import rocks.milspecsg.msrepository.api.cache.CacheService;
import rocks.milspecsg.msrepository.datastore.mongodb.MongoConfig;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface MongoRepository<
    T extends ObjectWithId<ObjectId>,
    C extends CacheService<ObjectId, T, Datastore, MongoConfig>>
    extends Repository<ObjectId, T, C, Datastore, MongoConfig> {

    CompletableFuture<WriteResult> delete(Query<T> query);

    CompletableFuture<Boolean> delete(Optional<Query<T>> query);

    Optional<UpdateOperations<T>> createUpdateOperations();

    Optional<UpdateOperations<T>> inc(String field, Number value);

    Optional<UpdateOperations<T>> inc(String field);

    Optional<Query<T>> asQuery();

    Optional<Query<T>> asQuery(ObjectId id);
}

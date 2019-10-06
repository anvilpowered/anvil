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
import org.mongodb.morphia.DeleteOptions;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import rocks.milspecsg.msrepository.api.cache.RepositoryCacheService;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;

import java.util.concurrent.CompletableFuture;

public interface MongoRepository<T extends ObjectWithId<ObjectId>, C extends RepositoryCacheService<ObjectId, T>> extends Repository<ObjectId, T, C> {

    CompletableFuture<WriteResult> deleteFromDS(Query<T> query, DeleteOptions deleteOptions);

    CompletableFuture<WriteResult> deleteFromDS(Query<T> query);

    UpdateOperations<T> createUpdateOperations();

    UpdateOperations<T> inc(String field, Number value);

    UpdateOperations<T> inc(String field);

    Query<T> asQuery();

    Query<T> asQuery(ObjectId id);

}

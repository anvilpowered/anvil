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

package rocks.milspecsg.msrepository.service.apirepository;

import com.google.inject.Inject;
import com.mongodb.WriteResult;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.QueryResults;
import org.mongodb.morphia.query.UpdateOperations;
import rocks.milspecsg.msrepository.api.cache.RepositoryCacheService;
import rocks.milspecsg.msrepository.api.repository.MongoRepository;
import rocks.milspecsg.msrepository.api.repository.Repository;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public interface ApiMongoRepository<T extends ObjectWithId<ObjectId>, C extends RepositoryCacheService<ObjectId, T>>
    extends Repository<ObjectId, T, C, Datastore>, MongoRepository<T, C> {

    @Override
    default CompletableFuture<Optional<T>> insertOne(T item) {
        return CompletableFuture.supplyAsync(() -> {
            Key<T> key;
            try {
                Optional<Datastore> optionalDataStore = getDataStoreContext().getDataStore();
                if (!optionalDataStore.isPresent()) {
                    return Optional.empty();
                }
                key = optionalDataStore.get().save(item);
            } catch (Exception e) {
                e.printStackTrace();
                return Optional.empty();
            }
            item.setId((ObjectId) key.getId());
            getRepositoryCacheService().ifPresent(cache -> cache.insertOne(item));
            return Optional.of(item);
        });
    }

    @Override
    default CompletableFuture<Optional<T>> getOne(ObjectId id) {
        return CompletableFuture.supplyAsync(() -> asQuery(id).map(QueryResults::get));
    }

    @Override
    default CompletableFuture<List<ObjectId>> getAllIds() {
        return CompletableFuture.supplyAsync(() ->
            asQuery()
                .map(q ->
                    q.project("_id", true)
                        .asList().stream().map(ObjectWithId::getId)
                        .collect(Collectors.toList())
                ).orElse(Collections.emptyList()));
    }

    @Override
    default CompletableFuture<WriteResult> delete(Query<T> query) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<Datastore> optionalDataStore = getDataStoreContext().getDataStore();
                if (!optionalDataStore.isPresent()) {
                    return WriteResult.unacknowledged();
                }
                return optionalDataStore.get().delete(query);
            } catch (Exception e) {
                e.printStackTrace();
                return WriteResult.unacknowledged();
            }
        });
    }

    @Override
    default Optional<UpdateOperations<T>> inc(String field, Number value) {
        return createUpdateOperations().map(u -> u.inc(field, value));
    }

    @Override
    default Optional<UpdateOperations<T>> inc(String field) {
        return inc(field, 1);
    }

    @Override
    default Optional<Query<T>> asQuery(ObjectId id) {
        return asQuery().map(q -> q.field("_id").equal(id));
    }

}

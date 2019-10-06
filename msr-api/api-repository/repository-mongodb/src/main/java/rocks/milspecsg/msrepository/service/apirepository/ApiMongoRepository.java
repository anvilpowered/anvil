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
import org.mongodb.morphia.DeleteOptions;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import rocks.milspecsg.msrepository.api.cache.RepositoryCacheService;
import rocks.milspecsg.msrepository.api.repository.MongoRepository;
import rocks.milspecsg.msrepository.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public abstract class ApiMongoRepository<T extends ObjectWithId<ObjectId>, C extends RepositoryCacheService<ObjectId, T>> extends ApiRepository<ObjectId, T, C, Datastore> implements MongoRepository<T, C> {

    public ApiMongoRepository(DataStoreContext<Datastore> mongoContext) {
        super(mongoContext);
    }

    @Override
    public CompletableFuture<Optional<T>> insertOneIntoDS(T item) {
        return CompletableFuture.supplyAsync(() -> {
            Key<T> key;
            try {
                Optional<Datastore> optionalDataStore = dataStoreContext.getDataStore();
                if (!optionalDataStore.isPresent()) {
                    return Optional.empty();
                }
                key = optionalDataStore.get().save(item);
            } catch (Exception e) {
                e.printStackTrace();
                return Optional.empty();
            }
            item.setId((ObjectId) key.getId());
            return Optional.of(item);
        });
    }

    @Override
    public CompletableFuture<Optional<T>> getOneFromDS(ObjectId id) {
        return CompletableFuture.supplyAsync(() -> Optional.ofNullable(asQuery(id).get()));
    }

    @Override
    public CompletableFuture<List<ObjectId>> getAllIds() {
        return CompletableFuture.supplyAsync(() -> asQuery().project("_id", true).asList().stream().map(ObjectWithId::getId).collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<WriteResult> deleteFromDS(Query<T> query, DeleteOptions deleteOptions) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<Datastore> optionalDataStore = dataStoreContext.getDataStore();
                if (!optionalDataStore.isPresent()) {
                    return WriteResult.unacknowledged();
                }
                return optionalDataStore.get().delete(query, deleteOptions);
            } catch (Exception e) {
                e.printStackTrace();
                return WriteResult.unacknowledged();
            }
        });
    }

    @Override
    public CompletableFuture<WriteResult> deleteFromDS(Query<T> query) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<Datastore> optionalDataStore = dataStoreContext.getDataStore();
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
    public CompletableFuture<Boolean> deleteOneFromDS(ObjectId id) {
        return deleteFromDS(asQuery(id)).thenApplyAsync(result -> result.wasAcknowledged() && result.getN() > 0);
    }

    @Override
    public UpdateOperations<T> inc(String field, Number value) {
        return createUpdateOperations().inc(field, value);
    }

    @Override
    public UpdateOperations<T> inc(String field) {
        return inc(field, 1);
    }

    @Override
    public Query<T> asQuery(ObjectId id) {
        return asQuery().field("_id").equal(id);
    }

}

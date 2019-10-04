package rocks.milspecsg.msrepository.service.apirepository;

import com.google.inject.Inject;
import com.mongodb.WriteResult;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.DeleteOptions;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import rocks.milspecsg.msrepository.api.repository.MongoRepository;
import rocks.milspecsg.msrepository.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public abstract class ApiMongoRepository<T extends ObjectWithId<ObjectId>> extends ApiRepository<ObjectId, T, Datastore> implements MongoRepository<T> {

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

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

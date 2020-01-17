package rocks.milspecsg.msrepository.common.repository;

import com.mongodb.WriteResult;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import rocks.milspecsg.msrepository.api.cache.CacheService;
import rocks.milspecsg.msrepository.api.model.ObjectWithId;
import rocks.milspecsg.msrepository.api.repository.CachedRepository;
import rocks.milspecsg.msrepository.api.storageservice.StorageService;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface CommonMongoCachedRepository<
    T extends ObjectWithId<ObjectId>,
    C extends CacheService<ObjectId, T, Datastore>>
    extends CommonMongoRepository<T>, CachedRepository<ObjectId, T, C, Datastore> {

    @Override
    default CompletableFuture<Optional<T>> insertOne(T item) {
        return applyFromDBToCacheConditionally(() -> CommonMongoRepository.super.insertOne(item).join(), StorageService::insertOne);
    }

    @Override
    default CompletableFuture<List<T>> insert(List<T> list) {
        return applyFromDBToCache(() -> CommonMongoRepository.super.insert(list).join(), StorageService::insert);
    }

    @Override
    default CompletableFuture<Optional<T>> getOne(ObjectId id) {
        return applyToBothConditionally(c -> c.getOne(id).join(), () -> CommonMongoRepository.super.getOne(id).join());
    }

    @Override
    default CompletableFuture<WriteResult> delete(Query<T> query) {
        return applyFromDBToCache(() -> CommonMongoRepository.super.delete(query).join(), (c, w) -> {
            try {
                if (w.wasAcknowledged() && w.getN() > 0) {
                    c.deleteOne((ObjectId) w.getUpsertedId());
                }
            } catch (RuntimeException ignored) {
            }
        });
    }
}

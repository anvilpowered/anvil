package rocks.milspecsg.msrepository.common.repository;

import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import jetbrains.exodus.entitystore.StoreTransaction;
import rocks.milspecsg.msrepository.api.cache.CacheService;
import rocks.milspecsg.msrepository.api.model.ObjectWithId;
import rocks.milspecsg.msrepository.api.repository.CachedRepository;
import rocks.milspecsg.msrepository.api.storageservice.StorageService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface CommonXodusCachedRepository<
    T extends ObjectWithId<EntityId>,
    C extends CacheService<EntityId, T, PersistentEntityStore>>
    extends CommonXodusRepository<T>, CachedRepository<EntityId, T, C, PersistentEntityStore> {

    @Override
    default CompletableFuture<Optional<T>> insertOne(T item) {
        return applyFromDBToCacheConditionally(() -> CommonXodusRepository.super.insertOne(item).join(), StorageService::insertOne);
    }

    @Override
    default CompletableFuture<List<T>> insert(List<T> list) {
        return applyFromDBToCache(() -> CommonXodusRepository.super.insert(list).join(), StorageService::insert);
    }

    @Override
    default CompletableFuture<Optional<T>> getOne(EntityId id) {
        return applyToBothConditionally(c -> c.getOne(id).join(), () -> CommonXodusRepository.super.getOne(id).join());
    }

    @Override
    default CompletableFuture<Boolean> delete(Function<? super StoreTransaction, ? extends Iterable<Entity>> query) {
        return applyFromDBToCacheConditionally(() ->
            getDataStoreContext().getDataStore().map(dataStore ->
                dataStore.computeInTransaction(txn -> {
                    List<EntityId> toDelete = new ArrayList<>();
                    query.apply(txn).forEach(entity -> {
                        toDelete.add(entity.getId());
                        entity.delete();
                    });
                    return txn.commit() ? toDelete : Collections.<EntityId>emptyList();
                })
            ), (c, toDelete) -> toDelete.forEach(id -> c.deleteOne(id).join()))
            .thenApplyAsync(result -> result.filter(list -> !list.isEmpty()).isPresent());
    }

    @Override
    default CompletableFuture<Boolean> deleteOne(EntityId id) {
        return applyFromDBToCache(() -> CommonXodusRepository.super.deleteOne(id).join(), (c, b) -> {
            if (b) {
                c.deleteOne(id).join();
            }
        });
    }
}

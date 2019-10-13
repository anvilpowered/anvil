package rocks.milspecsg.msrepository.service.apirepository;

import org.dizitart.no2.Document;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.WriteResult;
import org.dizitart.no2.objects.ObjectFilter;
import rocks.milspecsg.msrepository.api.cache.RepositoryCacheService;
import rocks.milspecsg.msrepository.api.repository.NitriteRepository;
import rocks.milspecsg.msrepository.api.repository.Repository;
import rocks.milspecsg.msrepository.api.storageservice.StorageService;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public interface ApiNitriteRepository<T extends ObjectWithId<NitriteId>, C extends RepositoryCacheService<NitriteId, T>>
    extends Repository<NitriteId, T, C, Nitrite>, NitriteRepository<T, C> {

    @Override
    @SuppressWarnings("unchecked")
    default CompletableFuture<Optional<T>> insertOne(T item) {
        return applyFromDBToCacheConditionally(() -> {
            Optional<Nitrite> optionalDataStore = getDataStoreContext().getDataStore();
            if (!optionalDataStore.isPresent()) {
                return Optional.empty();
            }
            try {
                optionalDataStore.get().getRepository(getTClass()).insert(item).forEach(item::setId);
            } catch (Exception e) {
                e.printStackTrace();
                return Optional.empty();
            }
            return Optional.of(item);
        }, StorageService::insertOne);
    }

    @Override
    @SuppressWarnings("unchecked")
    default CompletableFuture<List<T>> insert(List<T> list) {
        return applyFromDBToCache(() -> getDataStoreContext().getDataStore()
                .map(dataStore -> list.stream().filter(item -> {
                    try {
                        dataStore.getRepository(getTClass()).insert(item).forEach(item::setId);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                }).collect(Collectors.toList())).orElse(Collections.emptyList()),
            StorageService::insert);
    }

    @Override
    default CompletableFuture<Optional<T>> getOne(NitriteId id) {
        return applyToBothConditionally(c -> c.getOne(id).join(), () ->
            getDataStoreContext().getDataStore()
                .flatMap(dataStore -> asFilter(id)
                    .map(filter -> dataStore
                        .getRepository(getTClass())
                        .find(filter).firstOrDefault()
                    )
                )
        );
    }

    @Override
    default CompletableFuture<List<NitriteId>> getAllIds() {
        return CompletableFuture.supplyAsync(() ->
            getDataStoreContext().getDataStore().map(dataStore ->
                dataStore.getRepository(getTClass()).find().project(Document.createDocument("_id", null).getClass()).toList()
                    .stream().map(document -> (NitriteId) document.get("_id")).collect(Collectors.toList())
            ).orElse(Collections.emptyList()));
    }

    @Override
    default CompletableFuture<WriteResult> delete(ObjectFilter filter) {
        return applyFromDBToCache(() -> {
            try {
                Optional<Nitrite> optionalDataStore = getDataStoreContext().getDataStore();
                if (!optionalDataStore.isPresent()) {
                    return unacknowledged();
                }
                return optionalDataStore.get().getRepository(getTClass()).remove(filter);
            } catch (Exception e) {
                e.printStackTrace();
                return unacknowledged();
            }
        }, (c, w) -> {
            try {
                if (w.getAffectedCount() > 0) {
                    c.deleteOne(w.iterator().next());
                }
            } catch (Exception ignored) {
            }
        });
    }

    @Override
    default CompletableFuture<Boolean> deleteOne(NitriteId id) {
        return CompletableFuture.supplyAsync(() -> asFilter(id).filter(q -> delete(q).join().getAffectedCount() > 0).isPresent());
    }
    static WriteResult unacknowledged() {
        return new WriteResult() {
            @Override
            public int getAffectedCount() {
                return 0;
            }

            @Override
            public Iterator<NitriteId> iterator() {
                return Collections.emptyIterator();
            }
        };
    }

}

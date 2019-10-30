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

import io.jsondb.JsonDBOperations;
import org.bson.types.ObjectId;
import rocks.milspecsg.msrepository.api.cache.RepositoryCacheService;
import rocks.milspecsg.msrepository.api.repository.JsonRepository;
import rocks.milspecsg.msrepository.api.repository.Repository;
import rocks.milspecsg.msrepository.api.storageservice.StorageService;
import rocks.milspecsg.msrepository.datastore.json.JsonConfig;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public interface ApiJsonRepository<T extends ObjectWithId<String>, C extends RepositoryCacheService<String, T>>
    extends Repository<String, T, C, JsonDBOperations, JsonConfig>, JsonRepository<T, C> {

    @Override
    default CompletableFuture<Optional<Date>> getCreatedUtcDate(String id) {
        return getOne(id).thenApplyAsync(optionalT -> optionalT.map(ObjectWithId::getCreatedUtcDate));
    }

    @Override
    default CompletableFuture<Optional<T>> insertOne(T item) {
        return applyFromDBToCacheConditionally(() -> {
            Optional<JsonDBOperations> optionalDataStore = getDataStoreContext().getDataStore();
            if (!optionalDataStore.isPresent()) {
                return Optional.empty();
            }
            try {
                item.setId(ObjectId.get().toHexString());
                optionalDataStore.get().insert(item);
            } catch (Exception e) {
                e.printStackTrace();
                return Optional.empty();
            }
            return Optional.of(item);
        }, StorageService::insertOne);
    }

    @Override
    default CompletableFuture<List<T>> insert(List<T> list) {
        return applyFromDBToCache(() -> getDataStoreContext().getDataStore()
                .map(dataStore -> list.stream().filter(item -> {
                    try {
                        item.setId(ObjectId.get().toHexString());
                        dataStore.insert(item);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                }).collect(Collectors.toList())).orElse(Collections.emptyList()),
            StorageService::insert);
    }

    @Override
    default CompletableFuture<Optional<T>> getOne(String id) {
        return applyToBothConditionally(c -> c.getOne(id).join(), () ->
            getDataStoreContext().getDataStore()
                .flatMap(dataStore -> Optional.ofNullable(
                    dataStore.findById(id, dataStore.getCollectionName(getTClass()))
                )));
    }

    @Override
    default CompletableFuture<List<String>> getAllIds() {
        return CompletableFuture.supplyAsync(() ->
            getDataStoreContext().getDataStore()
                .map(j -> j.getCollection(getTClass())
                    .stream().map(ObjectWithId::getId).collect(Collectors.toList())).orElse(Collections.emptyList()));
    }

    @Override
    default CompletableFuture<Boolean> deleteOne(String id) {
        return applyFromDBToCache(() -> {
            try {
                Optional<JsonDBOperations> optionalDataStore = getDataStoreContext().getDataStore();
                if (!optionalDataStore.isPresent()) {
                    return false;
                }
                T toDelete = generateEmpty();
                toDelete.setId(id);
                String collection = optionalDataStore.get().getCollectionName(toDelete.getClass());
                optionalDataStore.get().remove(toDelete, collection);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }, (c, b) -> {
            try {
                if (b) {
                    c.deleteOne(id);
                }
            } catch (Exception ignored) {
            }
        });
    }
}

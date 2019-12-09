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

package rocks.milspecsg.msrepository.service.common.repository;

import org.dizitart.no2.Document;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.WriteResult;
import org.dizitart.no2.objects.Cursor;
import org.dizitart.no2.objects.ObjectFilter;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;
import rocks.milspecsg.msrepository.api.cache.CacheService;
import rocks.milspecsg.msrepository.api.repository.NitriteRepository;
import rocks.milspecsg.msrepository.api.storageservice.StorageService;
import rocks.milspecsg.msrepository.datastore.nitrite.NitriteConfig;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;
import rocks.milspecsg.msrepository.service.common.component.CommonNitriteComponent;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public interface CommonNitriteRepository<
    T extends ObjectWithId<NitriteId>,
    C extends CacheService<NitriteId, T, Nitrite, NitriteConfig>>
    extends NitriteRepository<T, C>, CommonNitriteComponent {

    @Override
    @SuppressWarnings("unchecked")
    default CompletableFuture<Optional<T>> insertOne(T item) {
        return applyFromDBToCacheConditionally(() -> {
            Optional<Nitrite> optionalDataStore = getDataStoreContext().getDataStore();
            if (!optionalDataStore.isPresent()) {
                return Optional.empty();
            }
            item.setId(NitriteId.newId());
            try {
                optionalDataStore.get().getRepository(getTClass()).insert(item);
            } catch (RuntimeException e) {
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
                    } catch (RuntimeException e) {
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
                .map(dataStore ->
                    dataStore
                        .getRepository(getTClass())
                        .find(asFilter(id)).firstOrDefault()
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
            } catch (RuntimeException e) {
                e.printStackTrace();
                return unacknowledged();
            }
        }, (c, w) -> {
            try {
                if (w.getAffectedCount() > 0) {
                    c.deleteOne(w.iterator().next());
                }
            } catch (RuntimeException ignored) {
            }
        });
    }

    @Override
    default CompletableFuture<Boolean> deleteOne(NitriteId id) {
        return delete(asFilter(id)).thenApplyAsync(wr -> wr.getAffectedCount() > 0);
    }

    @Override
    default ObjectFilter asFilter(NitriteId id) {
        return ObjectFilters.eq("_id", id);
    }

    @Override
    default Optional<Cursor<T>> asCursor(ObjectFilter filter) {
        return getDataStoreContext().getDataStore().map(nitrite -> nitrite.getRepository(getTClass()).find(filter));
    }

    @Override
    default Optional<Cursor<T>> asCursor(NitriteId id) {
        return asCursor(asFilter(id));
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

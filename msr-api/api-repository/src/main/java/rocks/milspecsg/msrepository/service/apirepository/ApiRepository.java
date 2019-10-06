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
import rocks.milspecsg.msrepository.api.cache.RepositoryCacheService;
import rocks.milspecsg.msrepository.api.repository.Repository;
import rocks.milspecsg.msrepository.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class ApiRepository<TKey, T extends ObjectWithId<TKey>, C extends RepositoryCacheService<TKey, T>, TDataStore> implements Repository<TKey, T, C> {

    protected DataStoreContext<TDataStore> dataStoreContext;

    public ApiRepository(DataStoreContext<TDataStore> dataStoreContext) {
        this.dataStoreContext = dataStoreContext;
    }

    @Override
    public CompletableFuture<Optional<T>> insertOne(T item) {
        return insertOneIntoDS(item).thenApplyAsync(dbResult -> {
            Optional<? extends RepositoryCacheService<TKey, T>> cache = getRepositoryCacheService();
            if (cache.isPresent()) {
                return dbResult.flatMap(t -> cache.flatMap(c -> c.save(t)));
            } else {
                return dbResult;
            }
        });
    }

    @Override
    public CompletableFuture<Optional<T>> getOne(TKey id) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<? extends RepositoryCacheService<TKey, T>> cache = getRepositoryCacheService();

            // try to find item from cache
            if (cache.isPresent()) {
                Optional<T> cacheResult = cache.get().getOne(id);
                if (cacheResult.isPresent()) {
                    return cacheResult;
                }
            }

            return getOneFromDS(id).thenApplyAsync(dbResult -> {
                if (cache.isPresent()) {
                    return dbResult.flatMap(t -> cache.flatMap(c -> c.save(t)));
                } else {
                    return dbResult;
                }
            }).join();
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteOne(TKey id) {
        return deleteOneFromDS(id).thenApplyAsync(dbResult -> (getRepositoryCacheService().map(c -> c.deleteOne(id).isPresent()).orElse(dbResult)));
    }

    @Override
    public CompletableFuture<Optional<T>> ifNotPresent(Function<C, Optional<T>> fromCache, Supplier<Optional<T>> fromDB) {
        return CompletableFuture.supplyAsync(getRepositoryCacheService().map(cache -> cache.ifNotPresent(fromCache, fromDB)).orElse(fromDB));
    }
}

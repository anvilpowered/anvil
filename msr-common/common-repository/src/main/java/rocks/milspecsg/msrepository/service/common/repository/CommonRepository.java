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

import rocks.milspecsg.msrepository.api.cache.CacheService;
import rocks.milspecsg.msrepository.api.repository.Repository;
import rocks.milspecsg.msrepository.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;
import rocks.milspecsg.msrepository.service.common.component.CommonComponent;
import rocks.milspecsg.msrepository.service.common.storageservice.CommonStorageService;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.*;

public abstract class CommonRepository<
    TKey,
    T extends ObjectWithId<TKey>,
    C extends CacheService<TKey, T, TDataStore>,
    TDataStore>
    extends CommonComponent<TKey, TDataStore>
    implements Repository<TKey, T, C, TDataStore>,
    CommonStorageService<TKey, T, TDataStore> {

    protected CommonRepository(DataStoreContext<TKey, TDataStore> dataStoreContext) {
        super(dataStoreContext);
    }

    @Override
    public CompletableFuture<Optional<Integer>> getCreatedUtcTimeStampSeconds(TKey id) {
        return getOne(id).thenApplyAsync(o -> o.map(ObjectWithId::getCreatedUtcTimeStampSeconds));
    }

    @Override
    public CompletableFuture<Optional<Long>> getCreatedUtcTimeStampMillis(TKey id) {
        return getOne(id).thenApplyAsync(o -> o.map(ObjectWithId::getCreatedUtcTimeStampMillis));
    }

    @Override
    public CompletableFuture<Optional<Date>> getCreatedUtcDate(TKey id) {
        return getOne(id).thenApplyAsync(o -> o.map(ObjectWithId::getCreatedUtcDate));
    }

    @Override
    public <K> CompletableFuture<K> applyFromDBToCache(Supplier<K> fromDB, BiConsumer<C, K> toCache) {
        return CompletableFuture.supplyAsync(fromDB).thenApplyAsync(k -> {
            getRepositoryCacheService().ifPresent(c -> toCache.accept(c, k));
            return k;
        });
    }

    @Override
    public <K> CompletableFuture<Optional<K>> applyFromDBToCacheConditionally(Supplier<Optional<K>> fromDB, BiConsumer<C, K> toCache) {
        return CompletableFuture.supplyAsync(fromDB).thenApplyAsync(optionalK -> {
            optionalK.ifPresent(k -> getRepositoryCacheService().ifPresent(c -> toCache.accept(c, k)));
            return optionalK;
        });
    }

    @Override
    public <K> CompletableFuture<K> applyFromDBThroughCache(Supplier<K> fromDB, BiFunction<C, K, K> cacheTransformer) {
        return CompletableFuture.supplyAsync(fromDB).thenApplyAsync(k -> getRepositoryCacheService().map(c -> cacheTransformer.apply(c, k)).orElse(k));
    }

    @Override
    public <K> CompletableFuture<Optional<K>> applyFromDBThroughCacheConditionally(Supplier<Optional<K>> fromDB, BiFunction<C, K, Optional<K>> cacheTransformer) {
        return CompletableFuture.supplyAsync(fromDB).thenApplyAsync(optionalK -> optionalK.flatMap(k -> getRepositoryCacheService().flatMap(c -> cacheTransformer.apply(c, k))));
    }

    @Override
    public <K> CompletableFuture<K> applyThroughBoth(Function<C, K> cacheTransformer, Function<Optional<K>, K> dbTransformer) {
        return CompletableFuture.supplyAsync(() -> dbTransformer.apply(getRepositoryCacheService().map(cacheTransformer)));
    }

    @Override
    public <K> CompletableFuture<Optional<K>> applyThroughBothConditionally(Function<C, Optional<K>> cacheTransformer, Function<K, K> dbTransformer) {
        return CompletableFuture.supplyAsync(() -> getRepositoryCacheService().flatMap(cacheTransformer).map(dbTransformer));
    }

    @Override
    public <K> CompletableFuture<Optional<K>> applyToBothConditionally(Function<C, Optional<K>> cacheTransformer, Supplier<Optional<K>> dbSupplier) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<K> optionalK = getRepositoryCacheService().flatMap(cacheTransformer);
            if (!optionalK.isPresent()) {
                return dbSupplier.get();
            }
            return optionalK;
        });
    }
}

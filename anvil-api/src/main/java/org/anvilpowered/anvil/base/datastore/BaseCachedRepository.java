/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.base.datastore;

import org.anvilpowered.anvil.api.datastore.CacheService;
import org.anvilpowered.anvil.api.datastore.CachedRepository;
import org.anvilpowered.anvil.api.model.ObjectWithId;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class BaseCachedRepository<
    TKey,
    T extends ObjectWithId<TKey>,
    C extends CacheService<TKey, T, TDataStore>,
    TDataStore>
    extends BaseRepository<TKey, T, TDataStore>
    implements CachedRepository<TKey, T, C, TDataStore> {

    @Override
    public <K> CompletableFuture<K> applyFromDBToCache(CompletableFuture<K> fromDB, BiConsumer<C, K> toCache) {
        return fromDB.thenApplyAsync(k -> {
            getCacheService().ifPresent(c -> toCache.accept(c, k));
            return k;
        });
    }

    @Override
    public <K> CompletableFuture<Optional<K>> applyFromDBToCacheConditionally(
        CompletableFuture<Optional<K>> fromDB,
        BiConsumer<C, K> toCache
    ) {
        return fromDB.thenApplyAsync(optionalK -> {
            optionalK.ifPresent(k -> getCacheService().ifPresent(c -> toCache.accept(c, k)));
            return optionalK;
        });
    }

    public <K> CompletableFuture<Boolean> updateCache(
        CompletableFuture<Boolean> fromDB,
        Function<C, CompletableFuture<Optional<K>>> cacheSupplier,
        Consumer<K> cacheSetter
    ) {
        return applyFromDBToCache(fromDB, (cache, dbResult) -> {
            if (dbResult) {
                cacheSupplier.apply(cache).join().ifPresent(cacheSetter);
            }
        });
    }

    @Override
    public <K> CompletableFuture<K> applyFromDBThroughCache(CompletableFuture<K> fromDB, BiFunction<C, K, K> cacheTransformer) {
        return fromDB.thenApplyAsync(k -> getCacheService().map(c -> cacheTransformer.apply(c, k)).orElse(k));
    }

    @Override
    public <K> CompletableFuture<Optional<K>> applyFromDBThroughCacheConditionally(
        CompletableFuture<Optional<K>> fromDB, BiFunction<C, K,
        Optional<K>> cacheTransformer
    ) {
        return fromDB.thenApplyAsync(optionalK ->
            optionalK.flatMap(k -> getCacheService().flatMap(c -> cacheTransformer.apply(c, k))));
    }

    @Override
    public <K> CompletableFuture<K> applyThroughBoth(
        Function<C, K> cacheTransformer,
        Function<Optional<K>, CompletableFuture<K>> dbTransformer
    ) {
        return dbTransformer.apply(getCacheService().map(cacheTransformer));
    }

    @Override
    public <K> CompletableFuture<Optional<K>> applyThroughBothConditionally(
        Function<C, Optional<K>> cacheTransformer,
        Function<K, CompletableFuture<Optional<K>>> dbTransformer
    ) {
        return getCacheService().flatMap(cacheTransformer).map(dbTransformer)
            .orElse(CompletableFuture.completedFuture(Optional.empty()));
    }

    @Override
    public <K> CompletableFuture<Optional<K>> applyToBothConditionally(
        Function<C, Optional<K>> cacheTransformer,
        Supplier<CompletableFuture<Optional<K>>> dbSupplier
    ) {
        Optional<K> optionalK = getCacheService().flatMap(cacheTransformer);
        if (optionalK.isPresent()) {
            return CompletableFuture.completedFuture(optionalK);
        }
        return dbSupplier.get();
    }
}

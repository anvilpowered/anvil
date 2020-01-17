package rocks.milspecsg.msrepository.common.repository;

import rocks.milspecsg.msrepository.api.cache.CacheService;
import rocks.milspecsg.msrepository.api.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.api.model.ObjectWithId;
import rocks.milspecsg.msrepository.api.repository.CachedRepository;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class CommonCachedRepository<
    TKey,
    T extends ObjectWithId<TKey>,
    C extends CacheService<TKey, T, TDataStore>,
    TDataStore>
    extends CommonRepository<TKey, T, TDataStore>
    implements CachedRepository<TKey, T, C, TDataStore> {

    protected CommonCachedRepository(DataStoreContext<TKey, TDataStore> dataStoreContext) {
        super(dataStoreContext);
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

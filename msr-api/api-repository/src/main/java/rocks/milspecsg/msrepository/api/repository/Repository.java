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

package rocks.milspecsg.msrepository.api.repository;

import rocks.milspecsg.msrepository.api.cache.RepositoryCacheService;
import rocks.milspecsg.msrepository.api.storageservice.DataStorageService;
import rocks.milspecsg.msrepository.datastore.DataStoreConfig;
import rocks.milspecsg.msrepository.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.*;

public interface Repository<
    TKey,
    T extends ObjectWithId<TKey>,
    C extends RepositoryCacheService<TKey, T>,
    TDataStore,
    TDataStoreConfig extends DataStoreConfig>
    extends DataStorageService<TKey, T> {

    default Optional<C> getRepositoryCacheService() {
        return Optional.empty();
    }

    DataStoreContext<TKey, TDataStore, TDataStoreConfig> getDataStoreContext();

    /**
     * <p>
     * Usually used when (updated) data from DB needs to be applied to cache
     * </p>
     * <p>
     * {@param toCache} will run if and only if:
     * </p>
     * <ul>
     *     <li>- Cache is present</li>
     * </ul>
     *
     * @param fromDB  {@link Supplier} retrieving data from DB
     * @param toCache {@link BiConsumer} applying DB data to cache
     * @return Result from DB after it has (optionally) been applied to cache
     */
    <K> CompletableFuture<K> applyFromDBToCache(Supplier<K> fromDB, BiConsumer<C, K> toCache);

    /**
     * <p>
     * Usually used when (updated) data from DB needs to be applied to cache
     * </p>
     * <p>
     * {@param toCache} will run if and only if:
     * </p>
     * <ul>
     *     <li>- Cache is present</li>
     *     <li>- {@link Optional} result from {@param fromDB} is present</li>
     * </ul>
     *
     * @param fromDB  {@link Supplier} retrieving data from DB
     * @param toCache {@link BiConsumer} applying DB data to cache.
     * @return Result from DB after it has (optionally) been applied to cache
     */
    <K> CompletableFuture<Optional<K>> applyFromDBToCacheConditionally(Supplier<Optional<K>> fromDB, BiConsumer<C, K> toCache);

    /**
     * <p>
     * Usually used for editing model data
     * </p>
     * <p>
     * {@param cacheTransformer} will run if and only if:
     * </p>
     * <ul>
     *     <li>- Cache is present</li>
     * </ul>
     *
     * @param fromDB           {@link Supplier} retrieving data from DB
     * @param cacheTransformer {@link BiFunction} applying DB data to cache
     * @return Result from cache
     */
    <K> CompletableFuture<K> applyFromDBThroughCache(Supplier<K> fromDB, BiFunction<C, K, K> cacheTransformer);

    /**
     * <p>
     * Usually used for editing model data
     * </p>
     * <p>
     * {@param cacheTransformer} will run if and only if:
     * </p>
     * <ul>
     *     <li>- Cache is present</li>
     *     <li>- {@link Optional} result from {@param fromDB} is present</li>
     * </ul>
     *
     * @param fromDB           {@link Supplier} retrieving data from DB
     * @param cacheTransformer {@link BiFunction} applying DB data to cache. Will only be run if {@link Optional<K>} is present
     * @return Result from cache if result and cache are present, otherwise from DB
     */
    <K> CompletableFuture<Optional<K>> applyFromDBThroughCacheConditionally(Supplier<Optional<K>> fromDB, BiFunction<C, K, Optional<K>> cacheTransformer);

    /**
     * <p>
     * Usually used for editing model data
     * </p>
     * <p>
     * {@param cacheTransformer} will run if and only if:
     * </p>
     * <ul>
     *     <li>- Cache is present</li>
     * </ul>
     *
     * @param cacheTransformer {@link Function} applying transformation to data in cache and returning new data
     * @param dbTransformer    {@link Function} retrieving data from db.
     *                         {@link Optional<K>} is the result of the cache function (will be empty if the cache or the result is not present)
     * @return Result from DB
     */
    <K> CompletableFuture<K> applyThroughBoth(Function<C, K> cacheTransformer, Function<Optional<K>, K> dbTransformer);

    /**
     * <p>
     * Usually used for retrieving or editing model data
     * </p>
     * <p>
     * {@param cacheTransformer} will run if and only if:
     * </p>
     * <ul>
     *     <li>- Cache is present</li>
     * </ul>
     * <p>
     * {@param dbTransformer} will run if and only if:
     * </p>
     * <ul>
     *     <li>- {@link Optional} result from {@param cacheTransformer} is present</li>
     * </ul>
     *
     * @param cacheTransformer {@link Function} applying transformation to data in cache and returning new data
     * @param dbTransformer    {@link Function} applying transformation to data in db
     * @return {@link K} result from cache
     */
    <K> CompletableFuture<Optional<K>> applyThroughBothConditionally(Function<C, Optional<K>> cacheTransformer, Function<K, K> dbTransformer);

    /**
     * <p>
     * Usually used for retrieving or editing model data
     * </p>
     * <p>
     * {@param cacheTransformer} will run if and only if:
     * </p>
     * <ul>
     *     <li>- Cache is present</li>
     * </ul>
     * {@param dbTransformer} will run if and only if:
     * </p>
     * <ul>
     *     <li>- {@link Optional} result from {@param cacheTransformer} is <strong>not</strong> present</li>
     * </ul>
     *
     * @param cacheTransformer {@link Function} applying transformation to data in cache and returning new data
     * @param dbSupplier       {@link Supplier} retrieving data from db. Will only be run if {@link Optional<K>} is not present
     * @return {@link K} result from cache if present, otherwise from db
     */
    <K> CompletableFuture<Optional<K>> applyToBothConditionally(Function<C, Optional<K>> cacheTransformer, Supplier<Optional<K>> dbSupplier);

}
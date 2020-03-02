/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
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

package org.anvilpowered.anvil.api.repository;

import org.anvilpowered.anvil.api.cache.CacheService;
import org.anvilpowered.anvil.api.model.ObjectWithId;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface CachedRepository<
    TKey,
    T extends ObjectWithId<TKey>,
    C extends CacheService<TKey, T, TDataStore>,
    TDataStore>
    extends Repository<TKey, T, TDataStore> {

    default Optional<C> getRepositoryCacheService() {
        return Optional.empty();
    }

    /**
     * <p>
     * Usually used when (updated) data from DB needs to be applied to cache
     * </p>
     * <p>
     * {@code toCache} will run if and only if:
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
     * {@code toCache} will run if and only if:
     * </p>
     * <ul>
     *     <li>- Cache is present</li>
     *     <li>- {@link Optional} result from {@code fromDB} is present</li>
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
     * {@code cacheTransformer} will run if and only if:
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
     * {@code cacheTransformer} will run if and only if:
     * </p>
     * <ul>
     *     <li>- Cache is present</li>
     *     <li>- {@link Optional} result from {@code fromDB} is present</li>
     * </ul>
     *
     * @param fromDB           {@link Supplier} retrieving data from DB
     * @param cacheTransformer {@link BiFunction} applying DB data to cache. Will only be run if {@link Optional} is present
     * @return Result from cache if result and cache are present, otherwise from DB
     */
    <K> CompletableFuture<Optional<K>> applyFromDBThroughCacheConditionally(Supplier<Optional<K>> fromDB, BiFunction<C, K, Optional<K>> cacheTransformer);

    /**
     * <p>
     * Usually used for editing model data
     * </p>
     * <p>
     * {@code cacheTransformer} will run if and only if:
     * </p>
     * <ul>
     *     <li>- Cache is present</li>
     * </ul>
     *
     * @param cacheTransformer {@link Function} applying transformation to data in cache and returning new data
     * @param dbTransformer    {@link Function} retrieving data from db.
     *                         {@link Optional} is the result of the cache function (will be empty if the cache or the result is not present)
     * @return Result from DB
     */
    <K> CompletableFuture<K> applyThroughBoth(Function<C, K> cacheTransformer, Function<Optional<K>, K> dbTransformer);

    /**
     * <p>
     * Usually used for retrieving or editing model data
     * </p>
     * <p>
     * {@code cacheTransformer} will run if and only if:
     * </p>
     * <ul>
     *     <li>- Cache is present</li>
     * </ul>
     * <p>
     * {@code dbTransformer} will run if and only if:
     * </p>
     * <ul>
     *     <li>- {@link Optional} result from {@code cacheTransformer} is present</li>
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
     * {@code cacheTransformer} will run if and only if:
     * </p>
     * <ul>
     *     <li>- Cache is present</li>
     * </ul>
     * {@code dbTransformer} will run if and only if:
     * <ul>
     *     <li>- {@link Optional} result from {@code cacheTransformer} is <strong>not</strong> present</li>
     * </ul>
     *
     * @param cacheTransformer {@link Function} applying transformation to data in cache and returning new data
     * @param dbSupplier       {@link Supplier} retrieving data from db. Will only be run if {@link Optional} is not present
     * @return {@link K} result from cache if present, otherwise from db
     */
    <K> CompletableFuture<Optional<K>> applyToBothConditionally(Function<C, Optional<K>> cacheTransformer, Supplier<Optional<K>> dbSupplier);
}

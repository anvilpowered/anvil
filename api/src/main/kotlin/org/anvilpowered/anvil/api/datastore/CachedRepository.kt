/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020-2021
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
package org.anvilpowered.anvil.api.datastore

import java.util.concurrent.CompletableFuture
import org.anvilpowered.anvil.api.model.ObjectWithId
import java.util.function.BiFunction
import java.util.function.BiConsumer
import java.util.Optional
import java.util.function.Function
import java.util.function.Supplier

@Deprecated("") // will probably break in 0.4
interface CachedRepository<TKey, T : ObjectWithId<TKey>?, C : CacheService<TKey, T, TDataStore>?, TDataStore> :
  Repository<TKey, T, TDataStore> {
  val repositoryCacheService: Optional<C>?
    get() = Optional.empty()

  /**
   *
   *
   * Usually used when (updated) data from DB needs to be applied to cache
   *
   *
   *
   * `toCache` will run if and only if:
   *
   *
   *  * - Cache is present
   *
   *
   * @param <K>     The type returned from the cache and database
   * @param fromDB  [Supplier] retrieving data from DB
   * @param toCache [BiConsumer] applying DB data to cache
   * @return Result from DB after it has (optionally) been applied to cache
  </K> */
  fun <K> applyFromDBToCache(fromDB: Supplier<K>?, toCache: BiConsumer<C, K>?): CompletableFuture<K>?

  /**
   *
   *
   * Usually used when (updated) data from DB needs to be applied to cache
   *
   *
   *
   * `toCache` will run if and only if:
   *
   *
   *  * - Cache is present
   *  * - [Optional] result from `fromDB` is present
   *
   *
   * @param <K>     The type returned from the cache and database
   * @param fromDB  [Supplier] retrieving data from DB
   * @param toCache [BiConsumer] applying DB data to cache.
   * @return Result from DB after it has (optionally) been applied to cache
  </K> */
  fun <K> applyFromDBToCacheConditionally(
    fromDB: Supplier<Optional<K>?>?,
    toCache: BiConsumer<C, K>?
  ): CompletableFuture<Optional<K>?>?

  /**
   *
   *
   * Usually used for editing model data
   *
   *
   *
   * `cacheTransformer` will run if and only if:
   *
   *
   *  * - Cache is present
   *
   *
   * @param <K>              The type returned from the cache and database
   * @param fromDB           [Supplier] retrieving data from DB
   * @param cacheTransformer [BiFunction] applying DB data to cache
   * @return Result from cache
  </K> */
  fun <K> applyFromDBThroughCache(fromDB: Supplier<K>?, cacheTransformer: BiFunction<C, K, K>?): CompletableFuture<K>?

  /**
   *
   *
   * Usually used for editing model data
   *
   *
   *
   * `cacheTransformer` will run if and only if:
   *
   *
   *  * - Cache is present
   *  * - [Optional] result from `fromDB` is present
   *
   *
   * @param <K>              The type returned from the cache and database
   * @param fromDB           [Supplier] retrieving data from DB
   * @param cacheTransformer [BiFunction] applying DB data to cache. Will only be run if [Optional] is present
   * @return Result from cache if result and cache are present, otherwise from DB
  </K> */
  fun <K> applyFromDBThroughCacheConditionally(
    fromDB: Supplier<Optional<K>?>?,
    cacheTransformer: BiFunction<C, K, Optional<K>?>?
  ): CompletableFuture<Optional<K>?>?

  /**
   *
   *
   * Usually used for editing model data
   *
   *
   *
   * `cacheTransformer` will run if and only if:
   *
   *
   *  * - Cache is present
   *
   *
   * @param <K>              The type returned by the cache and database
   * @param cacheTransformer [Function] applying transformation to data in cache and returning new data
   * @param dbTransformer    [Function] retrieving data from db.
   * [Optional] is the result of the cache function (will be empty if the cache or the result is not present)
   * @return Result from DB
  </K> */
  fun <K> applyThroughBoth(cacheTransformer: Function<C, K>?, dbTransformer: Function<Optional<K>?, K>?): CompletableFuture<K>?

  /**
   *
   *
   * Usually used for retrieving or editing model data
   *
   *
   *
   * `cacheTransformer` will run if and only if:
   *
   *
   *  * - Cache is present
   *
   *
   *
   * `dbTransformer` will run if and only if:
   *
   *
   *  * - [Optional] result from `cacheTransformer` is present
   *
   *
   * @param <K>              The type returned by the cache and database
   * @param cacheTransformer [Function] applying transformation to data in cache and returning new data
   * @param dbTransformer    [Function] applying transformation to data in db
   * @return [K] result from cache
  </K> */
  fun <K> applyThroughBothConditionally(
    cacheTransformer: Function<C, Optional<K>?>?,
    dbTransformer: Function<K, K>?
  ): CompletableFuture<Optional<K>?>?

  /**
   *
   *
   * Usually used for retrieving or editing model data
   *
   *
   *
   * `cacheTransformer` will run if and only if:
   *
   *
   *  * - Cache is present
   *
   * `dbTransformer` will run if and only if:
   *
   *  * - [Optional] result from `cacheTransformer` is **not** present
   *
   *
   * @param <K>              The type returned by the cache and database
   * @param cacheTransformer [Function] applying transformation to data in cache and returning new data
   * @param dbSupplier       [Supplier] retrieving data from db. Will only be run if [Optional] is not present
   * @return [K] result from cache if present, otherwise from db
  </K> */
  fun <K> applyToBothConditionally(
    cacheTransformer: Function<C, Optional<K>?>?,
    dbSupplier: Supplier<Optional<K>?>?
  ): CompletableFuture<Optional<K>?>?
}

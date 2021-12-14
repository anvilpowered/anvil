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
package org.anvilpowered.anvil.base.datastore

import org.anvilpowered.anvil.api.datastore.CacheService
import org.anvilpowered.anvil.api.datastore.CachedRepository
import org.anvilpowered.anvil.api.model.ObjectWithId
import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer
import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.Supplier

@Deprecated("") // will probably break in 0.4
abstract class BaseCachedRepository<TKey, T : ObjectWithId<TKey>?, C : CacheService<TKey, T, TDataStore>?, TDataStore> :
    BaseRepository<TKey, T, TDataStore>(), CachedRepository<TKey, T, C, TDataStore> {
    override fun <K> applyFromDBToCache(fromDB: Supplier<K>?, toCache: BiConsumer<C, K>?): CompletableFuture<K>? {
        return CompletableFuture.supplyAsync(fromDB).thenApplyAsync { k: K ->
            repositoryCacheService!!.ifPresent { c: C -> toCache!!.accept(c, k) }
            k
        }
    }

    override fun <K> applyFromDBToCacheConditionally(
        fromDB: Supplier<Optional<K>?>?,
        toCache: BiConsumer<C, K>?,
    ): CompletableFuture<Optional<K>?>? {
        return CompletableFuture.supplyAsync(fromDB).thenApplyAsync { optionalK: Optional<K>? ->
            optionalK!!.ifPresent { k: K -> repositoryCacheService!!.ifPresent { c: C -> toCache!!.accept(c, k) } }
            optionalK
        }
    }

    override fun <K> applyFromDBThroughCache(
        fromDB: Supplier<K>?,
        cacheTransformer: BiFunction<C, K, K>?,
    ): CompletableFuture<K>? {
        return CompletableFuture.supplyAsync(fromDB).thenApplyAsync { k: K ->
            repositoryCacheService!!.map { c: C -> cacheTransformer!!.apply(c, k) }
                .orElse(k)
        }
    }

    override fun <K> applyFromDBThroughCacheConditionally(
        fromDB: Supplier<Optional<K>?>?,
        cacheTransformer: BiFunction<C, K, Optional<K>?>?,
    ): CompletableFuture<Optional<K>?>? {
        return CompletableFuture.supplyAsync(fromDB).thenApplyAsync { optionalK: Optional<K>? ->
            optionalK!!.flatMap { k: K ->
                repositoryCacheService!!.flatMap { c: C ->
                    cacheTransformer!!.apply(c,
                        k)
                }
            }
        }
    }

    override fun <K> applyThroughBoth(
        cacheTransformer: Function<C, K>?,
        dbTransformer: Function<Optional<K>?, K>?,
    ): CompletableFuture<K>? {
        return CompletableFuture.supplyAsync { dbTransformer!!.apply(repositoryCacheService!!.map(cacheTransformer)) }
    }

    override fun <K> applyThroughBothConditionally(
        cacheTransformer: Function<C, Optional<K>?>?,
        dbTransformer: Function<K, K>?,
    ): CompletableFuture<Optional<K>?>? {
        return CompletableFuture.supplyAsync { repositoryCacheService!!.flatMap(cacheTransformer).map(dbTransformer) }
    }

    override fun <K> applyToBothConditionally(
        cacheTransformer: Function<C, Optional<K>?>?,
        dbSupplier: Supplier<Optional<K>?>?,
    ): CompletableFuture<Optional<K>?>? {
        return CompletableFuture.supplyAsync {
            val optionalK = repositoryCacheService!!.flatMap(cacheTransformer)
            if (!optionalK.isPresent) {
                return@supplyAsync dbSupplier!!.get()
            }
            optionalK
        }
    }
}

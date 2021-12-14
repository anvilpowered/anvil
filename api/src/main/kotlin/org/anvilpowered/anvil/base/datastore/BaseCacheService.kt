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

import org.anvilpowered.anvil.api.registry.Keys
import org.anvilpowered.anvil.api.registry.Registry
import java.util.Objects
import java.util.Optional
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Predicate
import java.util.function.Supplier

@Deprecated("") // will probably break in 0.4
abstract class BaseCacheService<TKey, T : ObjectWithId<TKey>?, TDataStore> protected constructor(protected var registry: Registry) :
    BaseRepository<TKey, T, TDataStore>(), CacheService<TKey, T, TDataStore> {
    protected var cache: ConcurrentMap<T, Long>
    private var timeoutSeconds: Int? = null

    init {
        registry.whenLoaded { registryLoaded() }.register()
        cache = ConcurrentHashMap<T, Long>()
    }

    private fun registryLoaded() {
        stopCacheInvalidationTask()
        val intervalSeconds = registry.getOrDefault<Int>(Keys.CACHE_INVALIDATION_INTERVAL_SECONDS)
        timeoutSeconds = registry.getOrDefault<Int>(Keys.CACHE_INVALIDATION_TIMOUT_SECONDS)
        startCacheInvalidationTask(intervalSeconds)
    }

    val cacheInvalidationTask: Runnable
        get() = Runnable {
            val toRemove: MutableList<T> = ArrayList()
            for (t in allAsSet) {
                if (System.currentTimeMillis() - cache.get(t) > timeoutSeconds!! * 1000L) {
                    toRemove.add(t)
                }
            }
            toRemove.forEach(Consumer { t: T -> this.delete(t) })
        }
    val allAsSet: Set<T>
        get() = cache.keys

    override fun insertOne(t: T): CompletableFuture<Optional<T>?>? {
        return CompletableFuture.supplyAsync<Optional<T>>(Supplier {
            if (t == null) return@supplyAsync Optional.empty<T>()
            cache.put(t, System.currentTimeMillis())
            Optional.of<T>(t)
        })
    }

    override fun insert(list: List<T>?): CompletableFuture<List<T>?>? {
        return CompletableFuture.supplyAsync<List<T>>(Supplier {
            list!!.stream().map(
                Function<T, T> { t: T -> insertOne(t).join().orElse(null) }).filter { obj: T -> Objects.nonNull(obj) }
                .collect(Collectors.toList<T>())
        })
    }

    override fun getOne(id: TKey): CompletableFuture<Optional<T>> {
        return CompletableFuture.supplyAsync<Optional<T>>(Supplier { getOne { dbo: T -> dbo.getId() == id } })
    }

    override fun deleteOne(id: TKey): CompletableFuture<Boolean?>? {
        return CompletableFuture.supplyAsync<Boolean>(Supplier { deleteOne { dbo: T -> dbo.getId() == id }!!.isPresent })
    }

    override val allIds: CompletableFuture<List<TKey>>
        get() = CompletableFuture.supplyAsync<List<TKey>>(Supplier<List<TKey>> {
            cache.keys.stream().map<TKey>(
                Function<T, TKey> { obj: T -> obj.getId() }).collect<List<TKey>, Any>(Collectors.toList<TKey>())
        })

    override fun deleteOne(predicate: Predicate<in T>?): Optional<T>? {
        return getOne(predicate)!!.flatMap { t: T -> this.delete(t) }
    }

    override fun delete(t: T): Optional<T>? {
        return if (cache.remove(t) == null) Optional.empty() else Optional.of(t)
    }

    override fun delete(predicate: Predicate<in T>?): List<T>? {
        return cache.keys.stream().filter(predicate).filter(Predicate { t: T -> cache.remove(t) != null })
            .collect<List<T>, Any>(Collectors.toList<T>())
    }

    override fun getAll(predicate: Predicate<in T>?): List<T>? {
        return allAsSet.stream().filter(predicate).collect(Collectors.toList<T>())
    }

    override fun getOne(predicate: Predicate<in T>?): Optional<T>? {
        return getAll(predicate)!!.stream().findAny()
    }
}

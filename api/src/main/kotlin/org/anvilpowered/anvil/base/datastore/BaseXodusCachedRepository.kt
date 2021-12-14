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

import jetbrains.exodus.entitystore.Entity
import jetbrains.exodus.entitystore.EntityId
import jetbrains.exodus.entitystore.PersistentEntityStore
import jetbrains.exodus.entitystore.StoreTransaction
import org.anvilpowered.anvil.api.datastore.CacheService
import org.anvilpowered.anvil.api.datastore.CachedRepository
import org.anvilpowered.anvil.api.datastore.Repository.deleteOne
import org.anvilpowered.anvil.api.datastore.Repository.getOne
import org.anvilpowered.anvil.api.model.ObjectWithId
import org.anvilpowered.anvil.base.datastore.BaseXodusRepository.insert
import org.anvilpowered.anvil.base.datastore.BaseXodusRepository.insertOne
import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier

@Deprecated("") // will probably break in 0.4
interface BaseXodusCachedRepository<T : ObjectWithId<EntityId?>?, C : CacheService<EntityId?, T, PersistentEntityStore?>?> :
    BaseXodusRepository<T>, CachedRepository<EntityId?, T, C, PersistentEntityStore?> {
    override fun insertOne(item: T): CompletableFuture<Optional<T>?> {
        return applyFromDBToCacheConditionally(Supplier<Optional<K>?> { super@BaseXodusRepository.insertOne(item).join() },
            BiConsumer { obj: C, item: K -> obj!!.insertOne(item) })!!
    }

    override fun insert(list: List<T>?): CompletableFuture<List<T>?> {
        return applyFromDBToCache(Supplier { super@BaseXodusRepository.insert(list).join() },
            BiConsumer { obj: C, list: K -> obj!!.insert(list) })!!
    }

    override fun getOne(id: EntityId): CompletableFuture<Optional<T>> {
        return applyToBothConditionally(Function<C, Optional<K>?> { c: C -> c!!.getOne(id).join() },
            Supplier<Optional<K>?> { super@BaseXodusRepository.getOne(id).join() })
    }

    override fun delete(query: Function<in StoreTransaction?, out Iterable<Entity?>?>?): CompletableFuture<Boolean> {
        return applyFromDBToCache<List<EntityId?>>(Supplier<List<EntityId>> {
            dataStoreContext!!.getDataStore()!!.computeInTransaction { txn: StoreTransaction ->
                val toDelete: MutableList<EntityId> = ArrayList()
                query!!.apply(txn)!!.forEach(Consumer { entity: Entity? ->
                    toDelete.add(entity!!.id)
                    entity.delete()
                })
                if (txn.commit()) toDelete else emptyList()
            }
        }, BiConsumer<C, List<EntityId>> { c: C, toDelete: List<EntityId> ->
            toDelete.forEach(
                Consumer { id: EntityId -> c!!.deleteOne(id)!!.join() })
        })
            .thenApplyAsync(Function { result: List<EntityId?> -> !result.isEmpty() })
    }

    override fun deleteOne(id: EntityId): CompletableFuture<Boolean?> {
        return applyFromDBToCache(Supplier { super@BaseXodusRepository.deleteOne(id).join() }, BiConsumer { c: C, b: K ->
            if (b) {
                c!!.deleteOne(id)!!.join()
            }
        })!!
    }
}

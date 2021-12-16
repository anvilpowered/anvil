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

import com.google.common.collect.ImmutableList
import jetbrains.exodus.entitystore.Entity
import jetbrains.exodus.entitystore.EntityId
import jetbrains.exodus.entitystore.EntityIterable
import jetbrains.exodus.entitystore.EntityIterator
import jetbrains.exodus.entitystore.EntityRemovedInDatabaseException
import jetbrains.exodus.entitystore.StoreTransaction
import org.anvilpowered.anvil.api.Anvil
import org.anvilpowered.anvil.api.datastore.XodusRepository
import org.anvilpowered.anvil.api.model.Mappable
import org.anvilpowered.anvil.api.model.ObjectWithId
import org.anvilpowered.anvil.api.util.TimeFormatService
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import java.util.function.Function

interface BaseXodusRepository<T : ObjectWithId<EntityId>> : XodusRepository<T>, BaseXodusComponent {

    override fun insertOne(item: T): CompletableFuture<T?> {
        return CompletableFuture.supplyAsync<T?> {
            dataStoreContext.getDataStore().computeInTransaction<T?> {
                val entity: Entity = it.newEntity(tClass.simpleName)
                (item as Mappable<Entity?>).writeTo(entity)
                item.setId(entity.id)
                if (it.commit()) item else null
            }
        }
    }

    override fun insert(list: List<T>): CompletableFuture<List<T>> {
        return CompletableFuture.supplyAsync {
            dataStoreContext.getDataStore().computeInTransaction {
                list.forEach(Consumer { item: T ->
                    val entity: Entity = it.newEntity(tClass.simpleName)
                    (item as Mappable<Entity?>).writeTo(entity)
                    item.setId(entity.id)
                })
                it.commit()
                list
            }
        }
    }

    override fun iterator(query: Function<in StoreTransaction, out Iterable<Entity>>): Iterator<T> {
        return dataStoreContext.getDataStore().computeInReadonlyTransaction {
            query.apply(it).asSequence<Entity>().map<Entity, T> { entity: Entity? ->
                val item: T = generateEmpty()
                (item as Mappable<Entity?>).readFrom(entity)
                item
            }.iterator()
        }
    }

    override fun getAll(query: Function<in StoreTransaction, out Iterable<Entity>>): CompletableFuture<List<T>> {
        return CompletableFuture.supplyAsync { ImmutableList.copyOf(iterator(query)) }
    }

    fun iterator(): Iterator<T> {
        return iterator { txn -> txn.getAll(tClass.simpleName) }
    }

    override val all: CompletableFuture<List<T>>
        get() = CompletableFuture.supplyAsync { ImmutableList.copyOf(iterator()) }

    override fun getOne(
        query: Function<in StoreTransaction, out Iterable<Entity>>,
    ): CompletableFuture<T?> {
        return CompletableFuture.supplyAsync {
            dataStoreContext.getDataStore().computeInReadonlyTransaction {
                val iterator: Iterator<Entity> = query.apply(it).iterator()
                if (iterator.hasNext()) {
                    val item: T = generateEmpty()
                    (item as Mappable<Entity?>).readFrom(iterator.next())
                    return@computeInReadonlyTransaction item
                } else {
                    return@computeInReadonlyTransaction null
                }
            }
        }
    }

    override fun getOne(id: EntityId): CompletableFuture<T?> {
        return CompletableFuture.supplyAsync {
            dataStoreContext.getDataStore().computeInReadonlyTransaction {
                val entity: Entity = try {
                    it.getEntity(id)
                } catch (ignored: EntityRemovedInDatabaseException) {
                    return@computeInReadonlyTransaction null
                }
                val item: T = generateEmpty()
                (item as Mappable<Entity?>).readFrom(entity)
                item
            }
        }
    }

    override fun getOne(createdUtc: Instant): CompletableFuture<T?> {
        return CompletableFuture.supplyAsync {
            dataStoreContext.getDataStore().computeInReadonlyTransaction {
                val entity = try {
                    val iterator = asQuery(createdUtc).apply(it).iterator()
                    if (!iterator.hasNext()) {
                        return@computeInReadonlyTransaction null
                    }
                    iterator.next()
                } catch (ignored: EntityRemovedInDatabaseException) {
                    return@computeInReadonlyTransaction null
                }
                val item: T = generateEmpty()
                (item as Mappable<Entity?>).readFrom(entity)
                item
            }
        }
    }

    // woah
    override val allIds: CompletableFuture<List<EntityId>>
        get() = CompletableFuture.supplyAsync {
            dataStoreContext.getDataStore().computeInReadonlyTransaction {
                val iterable: EntityIterable = it.getAll(tClass.simpleName)
                val iterator: EntityIterator = iterable.iterator()
                val roughCount: Long = iterable.roughCount
                val buffer = 50
                val listSize = if (roughCount > Int.MAX_VALUE - buffer) Int.MAX_VALUE - buffer // woah
                else (roughCount + buffer).toInt()
                val list: MutableList<EntityId> = ArrayList(listSize)
                while (iterator.hasNext()) {
                    list.add(iterator.nextId() ?: break)
                }
                if (iterator.shouldBeDisposed()) {
                    iterator.dispose()
                }
                list
            }
        }

    override fun delete(
        query: Function<in StoreTransaction, out Iterable<Entity>>,
    ): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync {
            dataStoreContext.getDataStore().computeInTransaction {
                var success = false
                for (entity in query.apply(it)) {
                    if (entity.delete()) {
                        success = true
                    }
                }
                it.commit() && success
            }
        }
    }

    override fun deleteOne(id: EntityId): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync {
            dataStoreContext.getDataStore().computeInTransaction {
                it.getEntity(id).delete() && it.commit()
            }
        }
    }

    override fun deleteOne(createdUtc: Instant): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync {
            dataStoreContext.getDataStore().computeInTransaction {
                val iterator: Iterator<Entity> = asQuery(createdUtc).apply(it).iterator()
                iterator.hasNext() && iterator.next().delete() && it.commit()
            }
        }

    }

    override fun update(
        query: Function<in StoreTransaction, out Iterable<Entity>>,
        update: Consumer<in Entity>,
    ): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync {
            dataStoreContext.getDataStore().computeInTransaction {
                query.apply(it).forEach(Consumer { e ->
                    update.accept(e)
                    val now: Instant = OffsetDateTime.now(ZoneOffset.UTC).toInstant()
                    e.setProperty("updatedUtcSeconds", now.epochSecond)
                    e.setProperty("updatedUtcNanos", now.nano)
                })
                it.commit()
            }
        }
    }

    override fun update(
        optionalQuery: Optional<Function<in StoreTransaction, out Iterable<Entity>>>,
        update: Consumer<in Entity>,
    ): CompletableFuture<Boolean> {
        return optionalQuery.map { q -> update(q, update) }
            .orElse(CompletableFuture.completedFuture(false))
    }

    override fun asQuery(
        id: EntityId,
    ): Function<in StoreTransaction, out Iterable<Entity>> {
        return Function<StoreTransaction, Iterable<Entity>> { listOf(it.getEntity(id)) }
    }

    override fun asQuery(
        createdUtc: Instant,
    ): Function<in StoreTransaction, out Iterable<Entity>> {
        return Function<StoreTransaction?, Iterable<Entity>> {
            it.find(tClass.simpleName, "createdUtcSeconds", createdUtc.epochSecond)
                .union(it.find(tClass.simpleName, "createdUtcNanos", createdUtc.nano))
        }
    }

    override fun asQueryForIdOrTime(idOrTime: String): Function<in StoreTransaction, out Iterable<Entity>>? {
        parse(idOrTime).also {
            if (it == null) {
                Anvil.environmentManager.coreEnvironment.injector.getInstance(TimeFormatService::class.java)
                    .parseInstant(idOrTime).also { time ->
                        if (time == null) {
                            return null
                        }
                        return Function<StoreTransaction, Iterable<Entity>> { txn ->
                            txn.find(tClass.simpleName, "createdUtcSeconds", time)
                        }
                    }
            }
            return asQuery(it!!)
        }
    }
}

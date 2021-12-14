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
import org.anvilpowered.anvil.api.Anvil
import java.util.Optional
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier

interface BaseXodusRepository<T : ObjectWithId<EntityId?>?> : XodusRepository<T>, BaseXodusComponent {
    override fun insertOne(item: T): CompletableFuture<Optional<T>?>? {
        return CompletableFuture.supplyAsync<Optional<T>>(Supplier {
            dataStoreContext!!.getDataStore()!!.computeInTransaction<Optional<T?>>(
                StoreTransactionalComputable<Optional<T>> { txn: StoreTransaction ->
                    val entity: Entity = txn.newEntity(tClass.getSimpleName())
                    (item as Mappable<Entity?>).writeTo(entity)
                    item.setId(entity.id)
                    if (txn.commit()) Optional.of(item) else Optional.empty()
                })
        }
        )
    }

    fun insert(list: List<T>?): CompletableFuture<List<T>?>? {
        return CompletableFuture.supplyAsync<List<T>>(Supplier {
            dataStoreContext!!.getDataStore()!!.computeInTransaction<List<T?>>(
                StoreTransactionalComputable<List<T>> { txn: StoreTransaction ->
                    list!!.forEach(Consumer { item: T ->
                        val entity: Entity = txn.newEntity(tClass.getSimpleName())
                        (item as Mappable<Entity?>).writeTo(entity)
                        item.setId(entity.id)
                    })
                    txn.commit()
                    list
                })
        }
        )
    }

    @JvmOverloads
    fun iterator(
        query: Function<in StoreTransaction?, out Iterable<Entity?>?>? = Function<StoreTransaction?, Iterable<Entity?>?> { txn: StoreTransaction? ->
            txn.getAll(tClass.getSimpleName())
        },
    ): Iterator<T>? {
        return dataStoreContext!!.getDataStore()!!.computeInReadonlyTransaction(
            StoreTransactionalComputable<Iterator<T>> { txn: StoreTransaction? ->
                query.apply(txn).asSequence<Entity>().map<Entity, T> { entity: Entity? ->
                    val item: T = generateEmpty()
                    (item as Mappable<Entity?>).readFrom(entity)
                    item
                }.iterator()
            }
        )
    }

    fun getAll(query: Function<in StoreTransaction?, out Iterable<Entity?>?>?): CompletableFuture<List<T>?>? {
        return CompletableFuture.supplyAsync<List<T>>(Supplier<List<T>> { ImmutableList.copyOf<T>(iterator(query)) })
    }

    val all: CompletableFuture<List<T>>
        get() = CompletableFuture.supplyAsync<List<T>>(Supplier<List<T>> { ImmutableList.copyOf<T>(iterator()) })

    fun getOne(
        query: Function<in StoreTransaction?, out Iterable<Entity?>?>?,
    ): CompletableFuture<Optional<T>?>? {
        return CompletableFuture.supplyAsync<Optional<T>>(Supplier {
            dataStoreContext!!.getDataStore()!!.computeInReadonlyTransaction<Optional<T?>>(
                StoreTransactionalComputable<Optional<T>> { txn: StoreTransaction? ->
                    val iterator: Iterator<Entity> = query.apply(txn).iterator()
                    if (iterator.hasNext()) {
                        val item: T = generateEmpty()
                        (item as Mappable<Entity?>).readFrom(iterator.next())
                        return@computeInReadonlyTransaction Optional.of(item)
                    } else {
                        return@computeInReadonlyTransaction Optional.empty<T>()
                    }
                })
        }
        )
    }

    override fun getOne(id: EntityId): CompletableFuture<Optional<T>> {
        return CompletableFuture.supplyAsync<Optional<T>>(Supplier {
            dataStoreContext!!.getDataStore()!!.computeInReadonlyTransaction<Optional<T?>>(
                StoreTransactionalComputable<Optional<T>> { txn: StoreTransaction ->
                    val entity: Entity
                    entity = try {
                        txn.getEntity(id)
                    } catch (ignored: EntityRemovedInDatabaseException) {
                        return@computeInReadonlyTransaction Optional.empty<T>()
                    }
                    val item: T = generateEmpty()
                    (item as Mappable<Entity?>).readFrom(entity)
                    Optional.of(item)
                })
        }
        )
    }

    override fun getOne(createdUtc: Instant?): CompletableFuture<Optional<T>?>? {
        return CompletableFuture.supplyAsync<Optional<T>>(Supplier {
            dataStoreContext!!.getDataStore()!!.computeInReadonlyTransaction<Optional<T?>>(
                StoreTransactionalComputable<Optional<T>> { txn: StoreTransaction? ->
                    val entity: Entity
                    entity = try {
                        val it: Iterator<Entity> = asQuery(createdUtc).apply(txn).iterator()
                        if (!it.hasNext()) {
                            return@computeInReadonlyTransaction Optional.empty<T>()
                        }
                        it.next()
                    } catch (ignored: EntityRemovedInDatabaseException) {
                        return@computeInReadonlyTransaction Optional.empty<T>()
                    }
                    val item: T = generateEmpty()
                    (item as Mappable<Entity?>).readFrom(entity)
                    Optional.of(item)
                })
        }
        )
    }

    // woah
    val allIds: CompletableFuture<List<EntityId>>
        get() = CompletableFuture.supplyAsync<List<EntityId>>(Supplier<List<EntityId?>> {
            dataStoreContext!!.getDataStore()!!.computeInReadonlyTransaction<List<EntityId?>>(
                StoreTransactionalComputable<List<EntityId>> { txn: StoreTransaction ->
                    val iterable: EntityIterable = txn.getAll(tClass.getSimpleName())
                    val iterator: EntityIterator = iterable.iterator()
                    val roughCount: Long = iterable.getRoughCount()
                    val buffer = 50
                    val listSize = if (roughCount > Int.MAX_VALUE - buffer) Int.MAX_VALUE - buffer // woah
                    else (roughCount + buffer).toInt()
                    val list: MutableList<EntityId> = ArrayList<EntityId>(listSize)
                    while (iterator.hasNext()) {
                        list.add(iterator.nextId())
                    }
                    if (iterator.shouldBeDisposed()) {
                        iterator.dispose()
                    }
                    list
                })
        }
        )

    fun delete(
        query: Function<in StoreTransaction?, out Iterable<Entity?>?>?,
    ): CompletableFuture<Boolean?>? {
        return CompletableFuture.supplyAsync<Boolean>(Supplier {
            dataStoreContext!!.getDataStore()!!.computeInTransaction<Boolean>(
                StoreTransactionalComputable<Boolean> { txn: StoreTransaction ->
                    var success = false
                    for (entity in query.apply(txn)) {
                        if (entity.delete()) {
                            success = true
                        }
                    }
                    txn.commit() && success
                })
        }
        )
    }

    override fun deleteOne(id: EntityId): CompletableFuture<Boolean?>? {
        return CompletableFuture.supplyAsync<Boolean>(Supplier {
            dataStoreContext!!.getDataStore()!!.computeInTransaction<Boolean>(
                StoreTransactionalComputable<Boolean> { txn: StoreTransaction -> txn.getEntity(id).delete() && txn.commit() })
        }
        )
    }

    override fun deleteOne(createdUtc: Instant?): CompletableFuture<Boolean?>? {
        return CompletableFuture.supplyAsync<Boolean>(Supplier {
            dataStoreContext!!.getDataStore()!!.computeInTransaction<Boolean>(
                StoreTransactionalComputable<Boolean> { txn: StoreTransaction ->
                    val it: Iterator<Entity> = asQuery(createdUtc).apply(txn).iterator()
                    it.hasNext() && it.next().delete() && txn.commit()
                })
        }
        )
    }

    fun update(
        query: Function<in StoreTransaction?, out Iterable<Entity?>?>?,
        update: Consumer<in Entity?>?,
    ): CompletableFuture<Boolean?>? {
        return CompletableFuture.supplyAsync<Boolean>(Supplier {
            dataStoreContext!!.getDataStore()!!.computeInTransaction<Boolean>(
                StoreTransactionalComputable<Boolean> { txn: StoreTransaction ->
                    query.apply(txn).forEach(Consumer { e: Entity ->
                        update!!.accept(e)
                        val now: Instant = OffsetDateTime.now(ZoneOffset.UTC).toInstant()
                        e.setProperty("updatedUtcSeconds", now.getEpochSecond())
                        e.setProperty("updatedUtcNanos", now.getNano())
                    })
                    txn.commit()
                })
        }
        )
    }

    override fun update(
        optionalQuery: Optional<Function<in StoreTransaction?, out Iterable<Entity?>?>?>?,
        update: Consumer<in Entity?>?,
    ): CompletableFuture<Boolean?>? {
        return optionalQuery.map<CompletableFuture<Boolean>>(Function<Function<in StoreTransaction?, out Iterable<Entity?>?>, CompletableFuture<Boolean>> { q: Function<in StoreTransaction?, out Iterable<Entity?>?>? ->
            update(q,
                update)
        })
            .orElse(CompletableFuture.completedFuture<Boolean>(false))
    }

    override fun asQuery(
        id: EntityId?,
    ): Function<in StoreTransaction?, out Iterable<Entity?>?>? {
        return Function<StoreTransaction?, Iterable<Entity?>?> { txn: StoreTransaction? -> listOf<Entity>(txn.getEntity(id)) }
    }

    override fun asQuery(
        createdUtc: Instant?,
    ): Function<in StoreTransaction?, out Iterable<Entity?>?>? {
        return Function<StoreTransaction?, Iterable<Entity?>?> { txn: StoreTransaction? ->
            txn.find(tClass.getSimpleName(),
                "createdUtcSeconds", createdUtc.getEpochSecond())
                .union(txn.find(tClass.getSimpleName(),
                    "createdUtcNanos", createdUtc.getNano()))
        }
    }

    override fun asQueryForIdOrTime(idOrTime: String?): Optional<Function<in StoreTransaction?, out Iterable<Entity?>?>?>? {
        return parse(idOrTime).map<Optional<Function<in StoreTransaction, out Iterable<Entity>>>>(
            Function<EntityId, Optional<Function<in StoreTransaction?, out Iterable<Entity?>?>>> { entityId: EntityId? ->
                Optional.of<Function<in StoreTransaction?, out Iterable<Entity?>?>>(asQuery(entityId))
            })
            .orElseGet(Supplier<Optional<Function<in StoreTransaction, out Iterable<Entity>>>> {
                Anvil.getEnvironmentManager().getCoreEnvironment()
                    .getInjector().getInstance(TimeFormatService::class.java).parseInstant(idOrTime)
                    .map { obj: Instant -> obj.getEpochSecond() }
                    .map { s -> { txn -> txn.find(tClass.getSimpleName(), "createdUtcSeconds", s) } }
            })
    }
}

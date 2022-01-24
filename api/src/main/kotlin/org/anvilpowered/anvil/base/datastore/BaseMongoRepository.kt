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

import dev.morphia.query.Query
import dev.morphia.query.Update
import dev.morphia.query.experimental.filters.Filters
import dev.morphia.query.experimental.updates.UpdateOperator
import dev.morphia.query.experimental.updates.UpdateOperators
import org.anvilpowered.anvil.api.Anvil
import org.anvilpowered.anvil.api.datastore.MongoRepository
import org.anvilpowered.anvil.api.model.ObjectWithId
import org.anvilpowered.anvil.api.util.TimeFormatService
import org.bson.types.ObjectId
import java.time.Instant
import java.util.Arrays
import java.util.concurrent.CompletableFuture
import java.util.stream.Collectors

interface BaseMongoRepository<T : ObjectWithId<ObjectId>> : MongoRepository<T>, BaseMongoComponent {

    override fun getCreatedUtc(id: ObjectId): CompletableFuture<Instant?> {
        return CompletableFuture.completedFuture(Instant.ofEpochSecond(id.timestamp.toLong()))
    }

    override fun insertOne(item: T): CompletableFuture<T?> {
        return CompletableFuture.supplyAsync {
            try {
                dataStoreContext.getDataStore().save(item)
            } catch (e: RuntimeException) {
                e.printStackTrace()
                return@supplyAsync null
            }
            item
        }
    }

    override fun insert(list: List<T>): CompletableFuture<List<T>> {
        return CompletableFuture.supplyAsync {
            list.stream().filter {
                try {
                    dataStoreContext.getDataStore().save(it)
                } catch (e: RuntimeException) {
                    e.printStackTrace()
                    return@filter false
                }
                true
            }.collect(Collectors.toList())
        }
    }

    override fun getOne(query: Query<T>): CompletableFuture<T?> {
        return CompletableFuture.supplyAsync { query.first() }
    }

    override fun getOne(id: ObjectId): CompletableFuture<T?> = getOne(asQuery(id))
    override fun getOne(createdUtc: Instant): CompletableFuture<T?> = getOne(asQuery(createdUtc))

    override val allIds: CompletableFuture<List<ObjectId>>
        get() = CompletableFuture.supplyAsync {
            asQuery().iterator().toList().stream().map {
                it.getId()
            }.collect(Collectors.toList())
        }

    override fun delete(query: Query<T>): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync {
            try {
                return@supplyAsync query.delete().deletedCount > 0
            } catch (e: RuntimeException) {
                e.printStackTrace()
                return@supplyAsync false
            }
        }
    }

    override fun deleteOne(id: ObjectId): CompletableFuture<Boolean> {
        return delete(asQuery(id))
    }

    override fun deleteOne(createdUtc: Instant): CompletableFuture<Boolean> {
        return delete(asQuery(createdUtc))
    }

    override fun createUpdateOperations(): Query<T> {
        return dataStoreContext.getDataStore().find(tClass)
    }

    override fun inc(field: String, value: Number): Update<T> {
        return createUpdateOperations().update(UpdateOperators.inc(field, value))
    }

    override fun inc(field: String): Update<T> {
        return inc(field, 1)
    }

    override operator fun set(field: String, value: Any): Update<T> {
        return createUpdateOperations().update(UpdateOperators.set(field, value))
    }

    override fun unSet(field: String): Update<T> {
        return createUpdateOperations().update(UpdateOperators.unset(field))
    }

    fun update(query: Query<T>, updateOperations: List<UpdateOperator>): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync { dataStoreContext.getDataStore().find(query.entityClass).update(updateOperations).execute().modifiedCount > 0 }
    }

    override fun update(query: Query<T>, update: Update<T>): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync { update.execute().modifiedCount > 0 }
    }

    override fun update(query: Query<T>?, vararg update: UpdateOperator): CompletableFuture<Boolean> {
        query.also {
            if (it == null) {
                return CompletableFuture.completedFuture(false)
            }
            return update(it, Arrays.stream(update).collect(Collectors.toList()))
        }
    }

    override val all: CompletableFuture<List<T>>
        get() = getAll(asQuery())

    override fun getAll(query: Query<T>): CompletableFuture<List<T>> {
        return CompletableFuture.supplyAsync(query::toList)
    }

    override fun asQuery(): Query<T> {
        return dataStoreContext.getDataStore().find(tClass)
    }

    override fun asQuery(id: ObjectId): Query<T> {
        return dataStoreContext.getDataStore().find(tClass).filter(Filters.all("_id", id))
    }

    override fun asQuery(createdUtc: Instant): Query<T> {
        val time = Integer.toHexString(createdUtc.epochSecond.toInt())
        return dataStoreContext.getDataStore().find(tClass).filter(
            Filters.gt("_id", ObjectId(time + "0000000000000000")),
            Filters.lt("_id", ObjectId(time + "ffffffffffffffff"))
        );
    }

    override fun asQueryForIdOrTime(idOrTime: String): Query<T>? {
        parse(idOrTime).also {
            if (it == null) {
                Anvil.getEnvironmentManager().coreEnvironment.injector.getInstance(TimeFormatService::class.java)
                    .parseInstant(idOrTime).also { time ->
                        if (time == null) {
                            return null
                        }
                        return asQuery(time)
                    }
            }
            return asQuery(it!!)
        }
    }
}
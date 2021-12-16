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

import dev.morphia.Datastore
import dev.morphia.query.Query
import dev.morphia.query.Update
import dev.morphia.query.experimental.updates.UpdateOperator
import org.anvilpowered.anvil.api.model.ObjectWithId
import org.bson.types.ObjectId
import java.time.Instant
import java.util.concurrent.CompletableFuture

interface MongoRepository<T : ObjectWithId<ObjectId>> : Repository<ObjectId, T, Datastore> {
    fun getOne(query: Query<T>): CompletableFuture<T?>
    fun getAll(query: Query<T>): CompletableFuture<List<T>>
    fun delete(query: Query<T>): CompletableFuture<Boolean>
    fun createUpdateOperations(): Query<T>
    fun inc(field: String, value: Number): Update<T>
    fun inc(field: String): Update<T>
    fun set(field: String, value: Any): Update<T>
    fun unSet(field: String): Update<T>
    fun update(query: Query<T>, update: Update<T>): CompletableFuture<Boolean>
    fun update(
        query: Query<T>?,
        vararg update: UpdateOperator
    ): CompletableFuture<Boolean>

    fun asQuery(): Query<T>
    fun asQuery(id: ObjectId): Query<T>
    fun asQuery(createdUtc: Instant): Query<T>
    fun asQueryForIdOrTime(idOrTime: String): Query<T>?
}

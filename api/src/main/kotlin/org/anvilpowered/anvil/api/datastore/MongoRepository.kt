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
import java.util.Optional

interface MongoRepository<T : ObjectWithId<ObjectId?>?> : Repository<ObjectId?, T, Datastore?> {
  override fun getOne(query: Query<T>?): CompletableFuture<Optional<T>?>?
  fun getAll(query: Query<T>?): CompletableFuture<List<T>?>?
  fun delete(query: Query<T>?): CompletableFuture<Boolean?>?
  fun createUpdateOperations(): UpdateOperations<T>?
  operator fun inc(field: String?, value: Number?): UpdateOperations<T>?
  operator fun inc(field: String?): UpdateOperations<T>?
  operator fun set(field: String?, value: Any?): UpdateOperations<T>?
  fun unSet(field: String?): UpdateOperations<T>?
  fun update(query: Query<T>?, updateOperations: UpdateOperations<T>?): CompletableFuture<Boolean?>?
  fun update(
    optionalQuery: Optional<Query<T>?>?,
    updateOperations: UpdateOperations<T>?
  ): CompletableFuture<Boolean?>?

  fun asQuery(): Query<T>?
  fun asQuery(id: ObjectId?): Query<T>?
  fun asQuery(createdUtc: Instant?): Query<T>?
  fun asQueryForIdOrTime(idOrTime: String?): Optional<Query<T>?>?
}

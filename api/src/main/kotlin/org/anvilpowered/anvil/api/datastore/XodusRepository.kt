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
import java.time.Instant
import org.anvilpowered.anvil.api.model.ObjectWithId
import jetbrains.exodus.entitystore.EntityId
import jetbrains.exodus.entitystore.PersistentEntityStore
import jetbrains.exodus.entitystore.StoreTransaction
import jetbrains.exodus.entitystore.Entity
import java.util.Optional
import java.util.function.Consumer
import java.util.function.Function

interface XodusRepository<T : ObjectWithId<EntityId?>?> : Repository<EntityId?, T, PersistentEntityStore?> {
  fun iterator(
    query: Function<in StoreTransaction?, out Iterable<Entity?>?>?
  ): Iterator<T>?

  fun getAll(
    query: Function<in StoreTransaction?, out Iterable<Entity?>?>?
  ): CompletableFuture<List<T>?>?

  fun getOne(
    query: Function<in StoreTransaction?, out Iterable<Entity?>?>?
  ): CompletableFuture<Optional<T>?>?

  fun delete(
    query: Function<in StoreTransaction?, out Iterable<Entity?>?>?
  ): CompletableFuture<Boolean?>?

  fun update(
    query: Function<in StoreTransaction?, out Iterable<Entity?>?>?,
    update: Consumer<in Entity?>?
  ): CompletableFuture<Boolean?>?

  fun update(
    optionalQuery: Optional<Function<in StoreTransaction?, out Iterable<Entity?>?>?>?,
    update: Consumer<in Entity?>?
  ): CompletableFuture<Boolean?>?

  fun asQuery(
    id: EntityId?
  ): Function<in StoreTransaction?, out Iterable<Entity?>?>?

  fun asQuery(
    createdUtc: Instant?
  ): Function<in StoreTransaction?, out Iterable<Entity?>?>?

  fun asQueryForIdOrTime(
    idOrTime: String?
  ): Optional<Function<in StoreTransaction?, out Iterable<Entity?>?>?>?
}

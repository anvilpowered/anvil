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

import org.anvilpowered.anvil.api.model.ObjectWithId
import java.lang.Runnable
import java.util.Optional
import java.util.function.Predicate

@Deprecated("") // will probably break in 0.4
interface CacheService<TKey, T : ObjectWithId<TKey>?, TDataStore> : Repository<TKey, T, TDataStore> {
  /**
   * Starts cache invalidation task
   *
   * @param intervalSeconds How often the cache invalidation task should run
   */
  fun startCacheInvalidationTask(intervalSeconds: Int?)

  /**
   * Stop cache invalidation task
   */
  fun stopCacheInvalidationTask()

  /**
   * @return Cache invalidation task
   */
  val cacheInvalidationTask: Runnable?

  /**
   * @return A set containing all parties in the cache
   */
  val allAsSet: Set<T>?

  /**
   * Deletes a [T] from the cache
   *
   * @param predicate of [T] to remove from cache
   * @return An optional containing the [T] if it was successfully removed
   */
  fun deleteOne(predicate: Predicate<in T>?): Optional<T>?

  /**
   * Deletes a [T] from the cache
   *
   * @param t [T] to remove from cache
   * @return An optional containing the [T] if it was successfully removed
   */
  fun delete(t: T): Optional<T>?

  /**
   * Deletes a [T] from the cache
   *
   * @param predicate of [T] to remove from cache
   * @return A list of successfully deleted elements
   */
  fun delete(predicate: Predicate<in T>?): List<T>?
  fun getAll(predicate: Predicate<in T>?): List<T>?
  fun getOne(predicate: Predicate<in T>?): Optional<T>?
}

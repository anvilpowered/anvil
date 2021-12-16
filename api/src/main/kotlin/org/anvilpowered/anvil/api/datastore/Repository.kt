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
import java.time.Instant
import java.util.Optional
import java.util.concurrent.CompletableFuture

interface Repository<TKey, T : ObjectWithId<TKey>?, TDataStore> : DBComponent<TKey, TDataStore> {

    /**
     * @return An empty [T]
     */
    fun generateEmpty(): T
    val tClass: Class<T>

    /**
     * @param id The id of the document
     * @return The time of creation of this document as an [Instant]
     */
    fun getCreatedUtc(id: TKey): CompletableFuture<Instant?> {
        return getOne(id).thenApplyAsync { it?.createdUtc }
    }

    /**
     * @param item A [document][T] to insert
     * @return An [Optional] containing the inserted [document][T] if successful, otherwise [Optional.empty]
     */
    fun insertOne(item: T): CompletableFuture<T?>

    /**
     * @param list A [List] of [documents][T] to insert
     * @return A [List] of all [documents][T] that were successfully inserted
     */
    fun insert(list: List<T>): CompletableFuture<List<T>>

    /**
     * @return A [List] of all [ids][TKey] in the repository
     */
    val allIds: CompletableFuture<List<TKey>>

    /**
     * @return A [List] of all [documents][T] in the repository
     */
    val all: CompletableFuture<List<T>>

    /**
     * Attempts to find a matching [document][T] with the provided [id][TKey]
     *
     * @param id An [id][TKey] to query the repository with
     * @return An [Optional] containing a matching [document][T] if successful, otherwise [Optional.empty]
     */
    fun getOne(id: TKey): CompletableFuture<T?>

    /**
     * Attempts to find the first [document][T] where [Instant.getEpochSecond] retrieved from
     * [ObjectWithId.getCreatedUtc] is equal to [Instant.getEpochSecond] of the provided [Instant]
     *
     * @param createdUtc An [Instant] to query the repository with
     * @return An [Optional] containing  if successful, otherwise [Optional.empty]
     */
    fun getOne(createdUtc: Instant): CompletableFuture<T?>

    /**
     * Attempts to find a matching [document][T] by parsing provided id or time.
     *
     *
     *
     * Attempts to parse the provided [Object] as an id. If parsing is successful, returns the
     * result of [.getOne].
     *
     *
     *
     *
     * If parsing as an id is unsuccessful, attempts to parse the provided [Object] as createdUtc.
     * If parsing is successful, returns the result of [.getOne]
     *
     *
     *
     *
     * Note: if parsing as id is successful but no document is found, will not attempt to parse as time
     *
     *
     * @param idOrTime An id or time to parse. Can be an instance of [TKey] or a [String] representation. May
     * also be wrapped in an [Optional]
     * @return An [Optional] containing a matching [document][T] if successful, otherwise [Optional.empty]
     * @see DBComponent.parseUnsafe
     * @see DBComponent.parse
     * @see TimeFormatService.parseInstantUnsafe
     * @see TimeFormatService.parseInstant
     */
    fun parseAndGetOne(idOrTime: Any): CompletableFuture<T?>

    /**
     * Attempts to delete a matching [document][T] with the provided [id][TKey]
     *
     * @param id An [id][TKey] to query the repository with
     * @return Whether or not an item was found and deleted
     */
    fun deleteOne(id: TKey): CompletableFuture<Boolean>

    /**
     * Attempts to delete the first [document][T] where [Instant.getEpochSecond] retrieved from
     * [ObjectWithId.getCreatedUtc] is equal to [Instant.getEpochSecond] of the provided [Instant]
     *
     * @param createdUtc An [Instant] to query the repository with
     * @return Whether a [document][T] was found and deleted
     */
    fun deleteOne(createdUtc: Instant): CompletableFuture<Boolean>

    /**
     * Attempts to delete a matching [document][T] by parsing provided id or time.
     *
     *
     *
     * Attempts to parse the provided [Object] as an id. If parsing is successful, returns the
     * result of [.deleteOne].
     *
     *
     *
     *
     * If parsing as an id is unsuccessful, attempts to parse the provided [Object] as createdUtc.
     * If parsing is successful, returns the result of [.deleteOne]
     *
     *
     *
     *
     * Note: if id parsing is successful but no document is found, will not attempt to parse as time
     *
     *
     * @param idOrTime [Object] to parse. Can be an instance of [TKey] or a [String] representation. May
     * also be wrapped in an [Optional]
     * @return Whether a [document][T] was found and deleted
     * @see DBComponent.parseUnsafe
     * @see DBComponent.parse
     * @see TimeFormatService.parseInstantUnsafe
     * @see TimeFormatService.parseInstant
     */
    fun parseAndDeleteOne(idOrTime: Any): CompletableFuture<Boolean>
}

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
package org.anvilpowered.anvil.api.coremember

import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.time.Instant
import jetbrains.exodus.entitystore.EntityId
import jetbrains.exodus.entitystore.PersistentEntityStore
import jetbrains.exodus.entitystore.StoreTransaction
import org.anvilpowered.anvil.api.model.coremember.CoreMember
import org.anvilpowered.anvil.api.datastore.XodusRepository
import jetbrains.exodus.entitystore.Entity
import java.util.function.Function

interface XodusCoreMemberRepository : CoreMemberRepository<EntityId?, PersistentEntityStore?>,
  XodusRepository<CoreMember<EntityId?>?> {
  /**
   * Creates a [Function] that acts as a `query`
   * matching documents whose property `userUUID`
   * matches the provided [UUID]
   *
   * @param userUUID [UUID] to create `query` for
   * @return `query` for the provided [UUID]
   */
  fun asQuery(userUUID: UUID?): Function<in StoreTransaction?, out Iterable<Entity?>?>?

  /**
   * Creates a [Function] that acts as a `query`
   * matching documents whose property `userName`
   * matches the provided [String]
   *
   * @param userName [String] to create `query` for
   * @return `query` for the provided [UUID]
   */
  fun asQuery(userName: String?): Function<in StoreTransaction?, out Iterable<Entity?>?>?

  /**
   * Creates a [Function] that acts as a `query`
   * matching documents whose property `ipAddress`
   * matches the provided [String]
   *
   * @param ipAddress [String] to create `query` for
   * @return `query` for the provided [UUID]
   */
  fun asQueryForIpAddress(ipAddress: String?): Function<in StoreTransaction?, out Iterable<Entity?>?>?

  /**
   * Updates the properties `banEndUtc`, `banReason`
   * and sets `banned` to `true` for documents that
   * match the provided `query`
   *
   * @param query  `query` to update documents for
   * @param endUtc [Instant] end of the ban
   * @param reason [String] reason for the ban
   * @return [CompletableFuture] wrapped [Boolean].
   * true if successful, otherwise false
   */
  fun ban(
    query: Function<in StoreTransaction?, out Iterable<Entity?>?>?,
    endUtc: Instant?,
    reason: String?
  ): CompletableFuture<Boolean?>?

  /**
   * Sets the property `banned` to `false` for
   * documents that match the provided `query`
   *
   * @param query `query` to update documents for
   * @return [CompletableFuture] wrapped [Boolean].
   * true if successful, otherwise false
   */
  fun unBan(query: Function<in StoreTransaction?, out Iterable<Entity?>?>?): CompletableFuture<Boolean?>?

  /**
   * Updates the properties `muteEndUtc`, `muteReason`
   * and sets `muted` to `true` for documents that
   * match the provided `query`
   *
   * @param query  `query` to update documents for
   * @param endUtc [Instant] end of the mute
   * @param reason [String] reason for the mute
   * @return [CompletableFuture] wrapped [Boolean].
   * true if successful, otherwise false
   */
  fun mute(
    query: Function<in StoreTransaction?, out Iterable<Entity?>?>?,
    endUtc: Instant?,
    reason: String?
  ): CompletableFuture<Boolean?>?

  /**
   * Sets the property `muted` to `false` for
   * documents that match the provided `query`
   *
   * @param query `query` to update documents for
   * @return [CompletableFuture] wrapped [Boolean].
   * true if successful, otherwise false
   */
  fun unMute(query: Function<in StoreTransaction?, out Iterable<Entity?>?>?): CompletableFuture<Boolean?>?

  /**
   * Updates the property `nickName` for
   * documents that match the provided `query`
   *
   * @param query    `query` to update documents for
   * @param nickName [String] new nickName
   * @return [CompletableFuture] wrapped [Boolean].
   * true if successful, otherwise false
   */
  fun setNickName(query: Function<in StoreTransaction?, out Iterable<Entity?>?>?, nickName: String?): CompletableFuture<Boolean?>?
  fun deleteNickName(query: Function<in StoreTransaction?, out Iterable<Entity?>?>?): CompletableFuture<Boolean?>?
}

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

import dev.morphia.Datastore
import dev.morphia.query.Query
import org.anvilpowered.anvil.api.datastore.MongoRepository
import org.anvilpowered.anvil.api.model.coremember.CoreMember
import org.bson.types.ObjectId
import java.time.Instant
import java.util.UUID
import java.util.concurrent.CompletableFuture

interface MongoCoreMemberRepository : CoreMemberRepository<ObjectId, Datastore>, MongoRepository<CoreMember<ObjectId>> {
    /**
     * Creates a [Query] matching documents whose
     * property `userUUID` matches the provided [UUID]
     *
     * @param userUUID [UUID] to create [Query] for
     * @return [Query] for the provided [UUID]
     */
    fun asQuery(userUUID: UUID): Query<CoreMember<ObjectId>>

    /**
     * Creates a [Query] matching documents whose
     * property `userName` matches the provided [String]
     *
     * @param userName [String] to create [Query] for
     * @return [Query] for the provided [String]
     */
    fun asQuery(userName: String): Query<CoreMember<ObjectId>>


    /**
     * Creates a [Query] matching documents whose
     * property `ipAddress` matches the provided [String]
     *
     * @param ipAddress [String] to create [Query] for
     * @return [Query] for the provided [String]
     */
    fun asQueryForIpAddress(ipAddress: String): Query<CoreMember<ObjectId>>

    /**
     * Updates the properties `banEndUtc`, `banReason`
     * and sets `banned` to `true` for documents that
     * match the provided [ObjectId]
     *
     * @param id  [ObjectId] to update documents for
     * @param endUtc [Instant] end of the ban
     * @param reason [String] reason for the ban
     * @return [CompletableFuture] wrapped [Boolean].
     * true if successful, otherwise false
     */
    override fun ban(id: ObjectId, endUtc: Instant, reason: String): CompletableFuture<Boolean>

    /**
     * Sets the property `banned` to `false` for
     * documents that match the provided [Query]
     *
     * @param query [Query] to update documents for
     * @return [CompletableFuture] wrapped [Boolean].
     * true if successful, otherwise false
     */
    fun unBan(query: Query<CoreMember<ObjectId>>): CompletableFuture<Boolean>

    /**
     * Updates the properties `muteEndUtc`, `muteReason`
     * and sets `muted` to `true` for documents that
     * match the provided [Query]
     *
     * @param query  [Query] to update documents for
     * @param endUtc [Instant] end of the mute
     * @param reason [String] reason for the mute
     * @return [CompletableFuture] wrapped [Boolean].
     * true if successful, otherwise false
     */
    fun mute(query: Query<CoreMember<ObjectId>>, endUtc: Instant, reason: String): CompletableFuture<Boolean>

    /**
     * Sets the property `muted` to `false` for
     * documents that match the provided [Query]
     *
     * @param query [Query] to update documents for
     * @return [CompletableFuture] wrapped [Boolean].
     * true if successful, otherwise false
     */
    fun unMute(query: Query<CoreMember<ObjectId>>): CompletableFuture<Boolean>

    /**
     * Updates the property `nickName` for
     * documents that match the provided [Query]
     *
     * @param query    [Query] to update documents for
     * @param nickName [String] new nickName
     * @return [CompletableFuture] wrapped [Boolean].
     * true if successful, otherwise false
     */
    fun setNickName(query: Query<CoreMember<ObjectId>>, nickName: String): CompletableFuture<Boolean>

    /**
     * Deletes the property `nickName` for
     * documents that match the provided [Boolean]
     *
     * @param query [Query] to update documents for
     * @return [CompletableFuture] wrapped [Boolean]
     * true if successful, otherwise false
     */
    fun deleteNickName(query: Query<CoreMember<ObjectId>>): CompletableFuture<Boolean>
}

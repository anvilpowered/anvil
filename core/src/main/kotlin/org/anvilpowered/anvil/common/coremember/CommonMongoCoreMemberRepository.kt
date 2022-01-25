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
package org.anvilpowered.anvil.common.coremember

import dev.morphia.Datastore
import dev.morphia.query.Query
import dev.morphia.query.experimental.updates.UpdateOperator
import dev.morphia.query.experimental.updates.UpdateOperators
import org.anvilpowered.anvil.api.coremember.MongoCoreMemberRepository
import org.anvilpowered.anvil.api.model.coremember.CoreMember
import org.anvilpowered.anvil.base.datastore.BaseMongoRepository
import org.bson.types.ObjectId
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import java.util.concurrent.CompletableFuture

class CommonMongoCoreMemberRepository : CommonCoreMemberRepository<ObjectId, Datastore>(), BaseMongoRepository<CoreMember<ObjectId>>, MongoCoreMemberRepository {

    override fun getOneOrGenerateForUser(userUUID: UUID, userName: String, ipAddress: String, flags: BooleanArray): CompletableFuture<CoreMember<ObjectId>?> {
        val length = flags.size
        require(length == 8) { "Flags must be an array of length 8" }
        for (i in 0 until length) {
            flags[i] = false
        }
        return getOneForUser(userUUID).thenApplyAsync {
            val now = OffsetDateTime.now(ZoneOffset.UTC).toInstant()
            if (it == null) {
                val member = generateEmpty()
                member.userUUID = userUUID
                member.userName = userName
                member.ipAddress = ipAddress
                member.lastJoinedUtc = now
                flags[0] = true
                return@thenApplyAsync insertOne(member).join()
            }
            val member = it
            val updateOperators = mutableListOf<UpdateOperator>()
            var updateName = false
            var updateIpAddress = false

            if (userName != member.userName) {
                updateOperators.add(UpdateOperators.set("userName", userName))
                updateName = true
            }
            if (ipAddress != member.ipAddress) {
                updateOperators.add(UpdateOperators.set("ipAddress", ipAddress))
                updateIpAddress = true
            }
            updateOperators.add(UpdateOperators.set("lastJoinedUtc", now))
            if (asQuery(member.getId()).update(updateOperators).execute().modifiedCount > 0) {
                if (updateName) {
                    member.userName = userName
                    flags[1] = true
                }
                if (updateIpAddress) {
                    member.ipAddress = ipAddress
                    flags[2] = true
                }
            }
            member
        }
    }

    override fun getOneForUser(userUUID: UUID): CompletableFuture<CoreMember<ObjectId>?> {
        return getOne(asQuery(userUUID))
    }

    override fun getOneForUser(userName: String): CompletableFuture<CoreMember<ObjectId>?> {
        return getOne(asQuery(userName))
    }

    override fun getForIpAddress(ipAddress: String): CompletableFuture<List<CoreMember<ObjectId>>> {
        return getAll(asQueryForIpAddress(ipAddress))
    }

    override fun ban(id: ObjectId, endUtc: Instant, reason: String): CompletableFuture<Boolean> {
        return ban(asQuery(id), endUtc, reason)
    }

    override fun banUser(userUUID: UUID, endUtc: Instant, reason: String): CompletableFuture<Boolean> {
        return ban(asQuery(userUUID), endUtc, reason)
    }

    override fun banUser(userName: String, endUtc: Instant, reason: String): CompletableFuture<Boolean> {
        return ban(asQuery(userName), endUtc, reason)
    }

    override fun banIpAddress(ipAddress: String, endUtc: Instant, reason: String): CompletableFuture<Boolean> {
        return ban(asQueryForIpAddress(ipAddress), endUtc, reason)
    }

    override fun unBan(id: ObjectId): CompletableFuture<Boolean> {
        return unBan(asQuery(id))
    }

    override fun unBanUser(userUUID: UUID): CompletableFuture<Boolean> {
        return unBan(asQuery(userUUID))
    }

    override fun unBanUser(userName: String): CompletableFuture<Boolean> {
        return unBan(asQuery(userName))
    }

    override fun unBanIpAddress(ipAddress: String): CompletableFuture<Boolean> {
        return unBan(asQueryForIpAddress(ipAddress))
    }

    override fun mute(id: ObjectId, endUtc: Instant, reason: String): CompletableFuture<Boolean> {
        return mute(asQuery(id), endUtc, reason)
    }

    override fun muteUser(userUUID: UUID, endUtc: Instant, reason: String): CompletableFuture<Boolean> {
        return mute(asQuery(userUUID), endUtc, reason)
    }

    override fun muteUser(userName: String, endUtc: Instant, reason: String): CompletableFuture<Boolean> {
        return mute(asQuery(userName), endUtc, reason)
    }

    override fun muteIpAddress(ipAddress: String, endUtc: Instant, reason: String): CompletableFuture<Boolean> {
        return mute(asQueryForIpAddress(ipAddress), endUtc, reason)
    }

    override fun unMute(id: ObjectId): CompletableFuture<Boolean> {
        return unMute(asQuery(id))
    }

    override fun unMuteUser(userUUID: UUID): CompletableFuture<Boolean> {
        return unMute(asQuery(userUUID))
    }

    override fun unMuteUser(userName: String): CompletableFuture<Boolean> {
        return unMute(asQuery(userName))
    }

    override fun unMuteIpAddress(ipAddress: String): CompletableFuture<Boolean> {
        return unMute(asQueryForIpAddress(ipAddress))
    }

    override fun setNickName(id: ObjectId, nickName: String): CompletableFuture<Boolean> {
        return setNickName(asQuery(id), nickName)
    }

    override fun setNickNameForUser(userUUID: UUID, nickName: String): CompletableFuture<Boolean> {
        return setNickName(asQuery(userUUID), nickName)
    }

    override fun setNickNameForUser(userName: String, nickName: String): CompletableFuture<Boolean> {
        return setNickName(asQuery(userName), nickName)
    }

    override fun deleteNickName(id: ObjectId): CompletableFuture<Boolean> {
        return deleteNickName(asQuery(id))
    }

    override fun deleteNickNameForUser(userUUID: UUID): CompletableFuture<Boolean> {
        return deleteNickName(asQuery(userUUID))
    }

    override fun deleteNickNameForUser(userName: String): CompletableFuture<Boolean> {
        return deleteNickName(asQuery(userName))
    }

    override fun asQuery(userUUID: UUID): Query<CoreMember<ObjectId>> {
        TODO("Remove all \"field\" usage")
        return asQuery().field("userUUID").equal(userUUID)
    }

    override fun asQuery(userName: String): Query<CoreMember<ObjectId>> {
        return asQuery().field("userName").containsIgnoreCase(userName)
    }

    override fun asQueryForIpAddress(ipAddress: String): Query<CoreMember<ObjectId>> {
        return asQuery().field("ipAddress").equal(ipAddress)
    }

    private fun ban(query: Query<CoreMember<ObjectId>>, endUtc: Instant, reason: String): CompletableFuture<Boolean> {
        val updateOperators: MutableList<UpdateOperator> = ArrayList()
        updateOperators.add(UpdateOperators.set("banned", true))
        updateOperators.add(UpdateOperators.set("banEndUtc", endUtc))
        updateOperators.add(UpdateOperators.set("banReason", reason))
        return update(query, updateOperators)
    }

    override fun unBan(query: Query<CoreMember<ObjectId>>): CompletableFuture<Boolean> {
        return update(query, set("banned", false))
    }

    override fun mute(query: Query<CoreMember<ObjectId>>, endUtc: Instant, reason: String): CompletableFuture<Boolean> {
        val updateOperators: MutableList<UpdateOperator> = ArrayList()
        updateOperators.add(UpdateOperators.set("muted", true))
        updateOperators.add(UpdateOperators.set("muteEndUtc", endUtc))
        updateOperators.add(UpdateOperators.set("reason", reason))
        return update(query, updateOperators)
    }

    override fun unMute(query: Query<CoreMember<ObjectId>>): CompletableFuture<Boolean> {
        return update(query, set("muted", false))
    }

    override fun setNickName(query: Query<CoreMember<ObjectId>>, nickName: String): CompletableFuture<Boolean> {
        return update(query, set("nickName", nickName))
    }

    override fun deleteNickName(query: Query<CoreMember<ObjectId>>): CompletableFuture<Boolean> {
        return update(query, unSet("nickName"))
    }
}
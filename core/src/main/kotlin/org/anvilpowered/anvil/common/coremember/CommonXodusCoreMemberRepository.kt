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

import com.google.inject.Inject
import jetbrains.exodus.entitystore.Entity
import jetbrains.exodus.entitystore.EntityId
import jetbrains.exodus.entitystore.PersistentEntityStore
import jetbrains.exodus.entitystore.StoreTransaction
import org.anvilpowered.anvil.api.coremember.XodusCoreMemberRepository
import org.anvilpowered.anvil.api.model.Mappable
import org.anvilpowered.anvil.api.model.coremember.CoreMember
import org.anvilpowered.anvil.base.datastore.BaseXodusRepository
import org.slf4j.Logger
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.function.Function

class CommonXodusCoreMemberRepository
    : CommonCoreMemberRepository<EntityId, PersistentEntityStore>(),
    BaseXodusRepository<CoreMember<EntityId>>, XodusCoreMemberRepository {

    @Inject
    private lateinit var logger: Logger

    override fun getOneOrGenerateForUser(userUUID: UUID, userName: String, ipAddress: String, flags: BooleanArray): CompletableFuture<CoreMember<EntityId>?> {
        val length = flags.size
        require(length == 8) { "Flags must be an array of length 8" }
        for (i in 0 until length) {
            flags[i] = false
        }
        return CompletableFuture.supplyAsync {
            dataStoreContext.getDataStore().computeInTransaction {
                val iterator = asQuery(userUUID).apply(it).iterator()
                if (!iterator.hasNext()) {
                    val member = generateEmpty()
                    member.userUUID = userUUID
                    member.userName = userName
                    member.ipAddress = ipAddress
                    member.lastJoinedUtc = OffsetDateTime.now(ZoneOffset.UTC).toInstant()
                    flags[0] = true
                    return@computeInTransaction insertOne(member).join()
                }
                val member = generateEmpty()
                val entity = iterator.next()
                (member as Mappable<Entity>).readFrom(entity)
                var updateUsername = false
                var updateIPAddress = false
                if (userName != member.userName) {
                    entity.setProperty("userName", userName)
                    updateUsername = true
                }
                if (ipAddress != member.ipAddress) {
                    entity.setProperty("ipAddress", ipAddress)
                    updateIPAddress = true
                }
                val now = OffsetDateTime.now(ZoneOffset.UTC).toInstant()
                val nowSeconds = now.epochSecond
                val nowNanos = now.nano
                entity.setProperty("lastJoinedUtcSeconds", nowSeconds)
                entity.setProperty("lastJoinedUtcNanos", nowNanos)
                entity.setProperty("updatedUtcSeconds", nowSeconds)
                entity.setProperty("updatedUtcNanos", nowNanos)
                if (it.commit()) {
                    if (updateUsername) {
                        member.userName = userName
                        flags[1] = true
                    }
                    if (updateIPAddress) {
                        member.ipAddress = ipAddress
                        flags[2] = true
                    }
                    member.lastJoinedUtc = now
                    return@computeInTransaction member
                }
                logger.error("Failed to update {} please report this on github!", userName)
                null
            }
        }
    }

    override fun getOneForUser(userUUID: UUID): CompletableFuture<CoreMember<EntityId>?> {
        return getOne(asQuery(userUUID))
    }

    override fun getOneForUser(userName: String): CompletableFuture<CoreMember<EntityId>?> {
        return getOne(asQuery(userName))
    }

    override fun ban(id: EntityId, endUtc: Instant, reason: String): CompletableFuture<Boolean> {
        return ban(asQuery(id), endUtc, reason)
    }

    override fun getForIpAddress(ipAddress: String): CompletableFuture<List<CoreMember<EntityId>>> {
        return getAll(asQueryForIpAddress(ipAddress))
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

    override fun unBan(id: EntityId): CompletableFuture<Boolean> {
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

    override fun mute(id: EntityId, endUtc: Instant, reason: String): CompletableFuture<Boolean> {
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

    override fun unMute(id: EntityId): CompletableFuture<Boolean> {
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

    override fun setNickName(id: EntityId, nickName: String): CompletableFuture<Boolean> {
        return setNickName(asQuery(id), nickName)
    }

    override fun setNickNameForUser(userUUID: UUID, nickName: String): CompletableFuture<Boolean> {
        return setNickName(asQuery(userUUID), nickName)
    }

    override fun setNickNameForUser(userName: String, nickName: String): CompletableFuture<Boolean> {
        return setNickName(asQuery(userName), nickName)
    }

    override fun deleteNickName(id: EntityId): CompletableFuture<Boolean> {
        return deleteNickName(asQuery(id))
    }

    override fun deleteNickNameForUser(userUUID: UUID): CompletableFuture<Boolean> {
        return deleteNickName(asQuery(userUUID))
    }

    override fun deleteNickNameForUser(userName: String): CompletableFuture<Boolean> {
        return deleteNickName(asQuery(userName))
    }

    override fun asQuery(userUUID: UUID): Function<in StoreTransaction, out Iterable<Entity>> {
        return Function<StoreTransaction, Iterable<Entity>> {
            it.find(tClass.simpleName, "userUUID", userUUID.toString())
        }
    }

    override fun asQuery(userName: String): Function<in StoreTransaction, out Iterable<Entity>> {
        return Function<StoreTransaction, Iterable<Entity>> {
            it.find(tClass.simpleName, "userName", userName)
        }
    }

    override fun asQueryForIpAddress(ipAddress: String): Function<in StoreTransaction, out Iterable<Entity>> {
        return Function<StoreTransaction, Iterable<Entity>> {
            it.find(tClass.simpleName, "ipAddress", ipAddress)
        }
    }

    override fun ban(query: Function<in StoreTransaction, out Iterable<Entity>>, endUtc: Instant, reason: String): CompletableFuture<Boolean> {
        return update(query) { e: Entity ->
            e.setProperty("banned", true)
            e.setProperty("banEndUtcSeconds", endUtc.epochSecond)
            e.setProperty("banEndUtcNanos", endUtc.nano)
            e.setProperty("banReason", reason)
        }
    }

    override fun unBan(query: Function<in StoreTransaction, out Iterable<Entity>>): CompletableFuture<Boolean> {
        return update(query) { e: Entity -> e.setProperty("banned", false) }
    }

    override fun mute(query: Function<in StoreTransaction, out Iterable<Entity>>, endUtc: Instant, reason: String): CompletableFuture<Boolean> {
        return update(query) { e: Entity ->
            e.setProperty("muted", true)
            e.setProperty("muteEndUtcSeconds", endUtc.epochSecond)
            e.setProperty("muteEndUtcNanos", endUtc.nano)
            e.setProperty("muteReason", reason)
        }
    }

    override fun unMute(query: Function<in StoreTransaction, out Iterable<Entity>>): CompletableFuture<Boolean> {
        return update(query) { e: Entity -> e.setProperty("muted", false) }
    }

    override fun setNickName(query: Function<in StoreTransaction, out Iterable<Entity>>, nickName: String): CompletableFuture<Boolean> {
        return update(query) { e: Entity -> e.setProperty("nickName", nickName) }
    }

    override fun deleteNickName(query: Function<in StoreTransaction, out Iterable<Entity>>): CompletableFuture<Boolean> {
        return update(query) { e: Entity -> e.deleteProperty("nickName") }
    }
}
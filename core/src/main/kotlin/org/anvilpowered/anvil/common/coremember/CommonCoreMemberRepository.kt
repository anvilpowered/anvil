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

import org.anvilpowered.anvil.api.coremember.CoreMemberRepository
import org.anvilpowered.anvil.api.model.coremember.CoreMember
import org.anvilpowered.anvil.base.datastore.BaseRepository
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import java.util.concurrent.CompletableFuture

abstract class CommonCoreMemberRepository<TKey, TDataStore> : BaseRepository<TKey, CoreMember<TKey>, TDataStore>(), CoreMemberRepository<TKey, TDataStore> {

    override val tClass: Class<CoreMember<TKey>> = dataStoreContext.getEntityClassUnsafe("coremember") as Class<CoreMember<TKey>>

    override fun getOneOrGenerateForUser(userUUID: UUID, userName: String, ipAddress: String): CompletableFuture<CoreMember<TKey>?> {
        return getOneOrGenerateForUser(userUUID, userName, ipAddress, booleanArrayOf(false, false, false, false, false, false, false, false))
    }

    override fun checkBanned(coreMember: CoreMember<*>): Boolean {
        if (coreMember.isBanned && coreMember.banEndUtc.isAfter(OffsetDateTime.now(ZoneOffset.UTC).toInstant())) {
            return true
        } else if (coreMember.isBanned) {
            unBanUser(coreMember.userUUID)
        }
        return false
    }

    override fun checkBanned(id: TKey): CompletableFuture<Boolean> = getOne(id).thenApplyAsync {
        checkBanned(it ?: return@thenApplyAsync false)
    }

    override fun checkBannedForUser(userUUID: UUID): CompletableFuture<Boolean> {
        return getOneForUser(userUUID).thenApply {
            if (it == null) {
                return@thenApply false
            }
            checkBanned(it)
        }
    }

    override fun checkBannedForUser(userName: String): CompletableFuture<Boolean> {
        return getOneForUser(userName).thenApply {
            if (it == null) {
                return@thenApply false
            }
            checkBanned(it)
        }
    }

    override fun checkMuted(coreMember: CoreMember<*>): Boolean {
        if (coreMember.isMuted && coreMember.muteEndUtc.isAfter(OffsetDateTime.now(ZoneOffset.UTC).toInstant())) {
            return true
        } else if (coreMember.isMuted) {
            unMuteUser(coreMember.userUUID)
        }
        return false
    }

    override fun checkMuted(id: TKey): CompletableFuture<Boolean> = getOne(id).thenApplyAsync {
        checkMuted(it ?: return@thenApplyAsync false)
    }

    override fun checkMutedForUser(userUUID: UUID): CompletableFuture<Boolean> {
        return getOneForUser(userUUID).thenApplyAsync {
            if (it == null) {
                return@thenApplyAsync false
            }
            checkMuted(it)
        }
    }

    override fun checkMutedForUser(userName: String): CompletableFuture<Boolean> {
        return getOneForUser(userName).thenApplyAsync {
            if (it == null) {
                return@thenApplyAsync false
            }
            checkMuted(it)
        }
    }
}
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
package org.anvilpowered.anvil.common.util

import com.google.common.collect.ImmutableList
import org.anvilpowered.anvil.api.Anvil.Companion.coreMemberRepository
import org.anvilpowered.anvil.api.util.UserService
import java.util.UUID
import java.util.concurrent.CompletableFuture

abstract class CommonUserService<TUser, TPlayer> protected constructor(
    private val userClass: Class<TUser>
) : UserService<TUser, TPlayer> {

    override fun matchPlayerNames(context: Array<String>, index: Int, length: Int): List<String> {
        return if (context.size == length) {
            matchPlayerNames(context[index])
        } else ImmutableList.of()
    }

    override fun getUUID(userName: String): CompletableFuture<UUID?> {
        return coreMemberRepository.getOneForUser(userName).thenApplyAsync { it?.userUUID }
    }

    override fun getUserName(userUUID: UUID): CompletableFuture<String?> {
        return coreMemberRepository.getOneForUser(userUUID).thenApplyAsync { it?.userName }
    }

    override fun <T> getUUIDSafe(obj: T): UUID {
        return if (userClass.isInstance(obj)) {
            getUUID(obj as TUser)!!
        } else {
            constant
        }
    }

    companion object {
        private val constant = UUID.nameUUIDFromBytes(ByteArray(0))
    }
}
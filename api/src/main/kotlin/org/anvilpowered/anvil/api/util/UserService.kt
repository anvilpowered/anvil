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
package org.anvilpowered.anvil.api.util

import java.util.UUID
import java.util.concurrent.CompletableFuture

/**
 * Service for translating UUIDs to UserNames or UserNames to UUIDs
 */
interface UserService<TUser, TPlayer> {
    operator fun get(userName: String): TUser?
    operator fun get(userUUID: UUID): TUser?
    fun getPlayer(userName: String): TPlayer?
    fun getPlayer(userUUID: UUID): TPlayer?

    /**
     * Attempts to find all matching userNames that start with the provided String (case-insensitive).
     *
     * @return A list of matching player names
     */
    fun matchPlayerNames(startsWith: String): List<String>

    /**
     * Attempts to find all matching userNames that start with the String at the provided index
     * of the provided array when it has the provided length.
     *
     * @param length The length of `context` for which to match the String at `index`
     * @return A list of matching player names
     */
    fun matchPlayerNames(context: Array<String>, index: Int, length: Int): List<String>
    fun onlinePlayers(): Collection<TPlayer>
    fun getUUID(userName: String): CompletableFuture<UUID?>
    fun getUserName(userUUID: UUID): CompletableFuture<String?>
    fun getUUID(user: TUser): UUID?

    /**
     * If the provided object has a [UUID], return it. Otherwise return a constant UUID that is
     * the same for all objects without a UUID.
     */
    fun <T> getUUIDSafe(obj: T): UUID
    fun getUserName(user: TUser): String
}

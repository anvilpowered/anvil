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
 *     along with this program.  If not, see <https://www.gnu.org/licenses/.
 */

package org.anvilpowered.anvil.api.server

import org.spongepowered.math.vector.Vector3d
import java.util.UUID

interface LocationService {
    /**
     * Attempts to get the current [BackendServer] that the provided [userUUID] is connected to.
     *
     * Note: Only available on the proxy side.
     *
     * @return The [BackendServer] if present, otherwise null.
     */
    fun getServer(userUUID: UUID): BackendServer?

    /**
     * Attempts to get the current [BackendServer] that the provided [userName] is connected to.
     *
     * Note: Only available on the proxy side.
     *
     * @return The [BackendServer] if present, otherwise null.
     */
    fun getServer(userName: String): BackendServer?

    /**
     * Attempts to get a [BackendServer] with a matching [serverName]
     *
     * Note: Only available on the proxy side.
     *
     * @return The [BackendServer] if present, otherwise null.
     */
    fun getServerForName(serverName: String): BackendServer?

    /**
     * Note: Only available on the proxy side.
     *
     * @return A list of all [BackendServer]'s
     */
    fun getServers(): List<BackendServer>

    /**
     * Attempts to get the current world name for the provided [userUUID]
     *
     * Note: Only available on the server side.
     *
     * @return The current world name that the provided [userUUID] is present on, otherwise null.
     */
    fun getWorldName(userUUID: UUID): String?

    /**
     * Attempts to get the current world name for the provided [userName]
     *
     * Note: Only available on the server side.
     *
     * @return The current world name that the provided [userName] is present on, otherwise null.
     */
    fun getWorldName(userName: String): String?

    /**
     * Attempts to get the current position of the provided [userUUID] represented by [Vector3d]
     *
     * Note: Only available on the server side.
     *
     * @return The position represented as [Vector3d] if present, otherwise null.
     */
    fun getPosition(userUUID: UUID): Vector3d?

    /**
     * Attempts to get the current position of the provided [userName] represented by [Vector3d]
     *
     * Note: Only available on the server side.
     *
     * @return The position represented as [Vector3d] if present, otherwise null.
     */
    fun getPosition(userName: String): Vector3d?

    /**
     * Attempts to teleport the [sourceUserUUID] to the [targetUserUUID]
     *
     * Note: Only available on the server side.
     *
     * @return [Boolean]
     * true if successful, otherwise false
     */
    suspend fun teleport(sourceUserUUID: UUID, targetUserUUID: UUID): Boolean
}

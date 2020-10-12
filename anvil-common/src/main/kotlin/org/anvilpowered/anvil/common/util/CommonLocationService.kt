/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020
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

import com.flowpowered.math.vector.Vector3d
import org.anvilpowered.anvil.api.util.LocationService
import java.util.Optional
import java.util.UUID
import java.util.concurrent.CompletableFuture

abstract class CommonLocationService : LocationService {

    override fun getServerName(userUUID: UUID): Optional<String> {
        return Optional.empty()
    }

    override fun getServerName(userName: String): Optional<String> {
        return Optional.empty()
    }

    override fun getWorldName(userUUID: UUID): Optional<String> {
        return Optional.empty()
    }

    override fun getWorldName(userName: String): Optional<String> {
        return Optional.empty()
    }

    override fun getPosition(userUUID: UUID): Optional<Vector3d>? {
        return Optional.empty()
    }

    override fun getPosition(userName: String): Optional<Vector3d>? {
        return Optional.empty()
    }

    override fun teleport(teleportingUserUUID: UUID, targetUserUUID: UUID): CompletableFuture<Boolean> {
        return CompletableFuture.completedFuture(false)
    }

    override fun setServer(userUUID: UUID, serverName: String): CompletableFuture<Boolean> {
        return CompletableFuture.completedFuture(false)
    }

    override fun setServer(userName: String, serverName: String): CompletableFuture<Boolean> {
        return CompletableFuture.completedFuture(false)
    }
}

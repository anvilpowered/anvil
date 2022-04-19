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
package org.anvilpowered.anvil.paper.server

import com.google.inject.Inject
import org.anvilpowered.anvil.api.util.UserService
import org.anvilpowered.anvil.core.server.CommonLocationService
import org.bukkit.entity.Player
import org.spongepowered.math.vector.Vector3d
import java.util.UUID

class PaperLocationService : CommonLocationService() {

    @Inject
    private lateinit var userService: UserService<Player, Player>

    override suspend fun teleport(sourceUserUUID: UUID, targetUserUUID: UUID): Boolean {
        val teleporter = userService[sourceUserUUID]
        val target = userService[targetUserUUID]
        return if (teleporter != null && target != null) {
            teleporter.teleport(target.location)
        } else false
    }

    override fun getWorldName(userUUID: UUID): String? {
        return userService[userUUID]?.world?.name
    }

    override fun getWorldName(userName: String): String? {
        return userService[userName]?.world?.name
    }

    private fun extractCoords(player: Player?): Vector3d? {
        if (player == null) {
            return null
        }
        val pos = player.location
        return Vector3d(pos.x, pos.y, pos.z)
    }

    override fun getPosition(userUUID: UUID): Vector3d? = extractCoords(userService[userUUID])
    override fun getPosition(userName: String): Vector3d? = extractCoords(userService[userName])
}

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
package org.anvilpowered.anvil.sponge.server

import com.google.inject.Inject
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.anvilpowered.anvil.api.util.UserService
import org.anvilpowered.anvil.core.server.CommonLocationService
import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.entity.living.player.server.ServerPlayer
import org.spongepowered.math.vector.Vector3d
import java.util.UUID

class SpongeLocationService : CommonLocationService() {

    @Inject
    private lateinit var userService: UserService<User, ServerPlayer>

    override suspend fun teleport(sourceUserUUID: UUID, targetUserUUID: UUID): Boolean {
        val source = userService[sourceUserUUID]
        val target = userService[targetUserUUID]
        return source != null && target != null && source.setLocation(target.worldKey(), target.position())
    }

    private fun User.getWorldName(): String? {
        val world = Sponge.server().worldManager().world(this.worldKey())
        return if (world.isPresent) {
            PlainTextComponentSerializer.plainText().serialize(world.get().properties().displayName().orElse(Component.text("ERROR")))
        } else {
            null
        }
    }

    override fun getWorldName(userUUID: UUID): String? = userService[userUUID]?.getWorldName()
    override fun getWorldName(userName: String): String? = userService[userName]?.getWorldName()
    override fun getPosition(userUUID: UUID): Vector3d? = userService[userUUID]?.position()
    override fun getPosition(userName: String): Vector3d? = userService[userName]?.position()
}

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
package org.anvilpowered.anvil.sponge8.server

import com.google.inject.Inject
import java.util.Optional
import java.util.UUID
import java.util.concurrent.CompletableFuture
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer
import org.anvilpowered.anvil.api.util.UserService
import org.anvilpowered.anvil.common.server.CommonLocationService
import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.entity.living.player.server.ServerPlayer
import org.spongepowered.math.vector.Vector3d

class Sponge8LocationService : CommonLocationService() {

  @Inject
  private lateinit var userService: UserService<User, ServerPlayer>

  override fun teleport(teleportingUserUUID: UUID, targetUserUUID: UUID): CompletableFuture<Boolean> {
    val teleporter = userService[teleportingUserUUID].orElse(null)
    val target = userService[targetUserUUID].orElse(null)
    return CompletableFuture.completedFuture(
      teleporter != null && target != null && teleporter.setLocation(target.worldKey, target.position)
    )
  }

  private fun Optional<User>.getWorldName(): Optional<String> =
    flatMap { Sponge.getServer().worldManager.world(it.worldKey) }
      .flatMap { it.properties.displayName() }
      .map { PlainComponentSerializer.plain().serialize(it) }

  private fun Optional<User>.getPosition(): Optional<Vector3d> = map { it.position }
  override fun getWorldName(userUUID: UUID): Optional<String> = userService[userUUID].getWorldName()
  override fun getWorldName(userName: String): Optional<String> = userService[userName].getWorldName()
  override fun getPosition(userUUID: UUID): Optional<Vector3d> = userService[userUUID].getPosition()
  override fun getPosition(userName: String): Optional<Vector3d> = userService[userName].getPosition()
}

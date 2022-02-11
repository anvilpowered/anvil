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
package org.anvilpowered.anvil.sponge.util

import org.anvilpowered.anvil.core.util.CommonUserService
import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.entity.living.player.server.ServerPlayer
import java.util.UUID
import java.util.concurrent.CompletableFuture
import kotlin.streams.asSequence

class SpongeUserService : CommonUserService<User, ServerPlayer>(User::class.java) {
    override val onlinePlayers: Collection<ServerPlayer> = Sponge.server().onlinePlayers()
    override fun get(userName: String): User? = Sponge.server().userManager().load(userName).join().orElse(null)
    override fun get(userUUID: UUID): User? = Sponge.server().userManager().load(userUUID).join().orElse(null)
    override fun getPlayer(userName: String): ServerPlayer? = Sponge.server().player(userName).orElse(null)
    override fun getPlayer(userUUID: UUID): ServerPlayer? = Sponge.server().player(userUUID).orElse(null)
    override fun getUUID(user: User): UUID = user.uniqueId()
    override fun getUserName(user: User): String = user.name()

    override fun matchPlayerNames(startsWith: String): List<String> {
        return Sponge.server().userManager().streamOfMatches(startsWith).asSequence()
            .map { it.name().orElse(null) }.filter { it != null }.toList()
    }

    override fun getUUID(userName: String): CompletableFuture<UUID?> {
        return CompletableFuture.completedFuture(getPlayer(userName)?.user()?.uniqueId())
    }

    override fun getUserName(userUUID: UUID): CompletableFuture<String?> {
        return CompletableFuture.completedFuture(getPlayer(userUUID)?.user()?.name())
    }
}

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
package org.anvilpowered.anvil.velocity.util

import com.google.inject.Inject
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import org.anvilpowered.anvil.core.util.CommonUserService
import java.util.Locale
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.stream.Collectors

class VelocityUserService @Inject constructor() : CommonUserService<Player, Player>(Player::class.java) {

    @Inject
    private lateinit var proxyServer: ProxyServer

    override fun onlinePlayers(): Collection<Player> {
        return proxyServer.allPlayers
    }
    override fun getPlayer(userName: String): Player? = get(userName)
    override fun get(userUUID: UUID): Player? = proxyServer.getPlayer(userUUID).orElse(null)
    override fun get(userName: String): Player? = proxyServer.getPlayer(userName).orElse(null)
    override fun getPlayer(userUUID: UUID): Player? = get(userUUID)
    override fun getUUID(player: Player): UUID = player.uniqueId
    override fun getUserName(player: Player): String = player.username

    override fun matchPlayerNames(startsWith: String): List<String> {
        val startsWithLowerCase = startsWith.lowercase(Locale.getDefault())
        return onlinePlayers().stream()
            .map { obj: Player -> obj.username }
            .filter { name: String -> name.lowercase(Locale.getDefault()).startsWith(startsWithLowerCase) }
            .collect(Collectors.toList())
    }

    override fun getUUID(userName: String): CompletableFuture<UUID?> {
        return CompletableFuture.completedFuture(getPlayer(userName)?.uniqueId)
    }

    override fun getUserName(userUUID: UUID): CompletableFuture<String?> {
        return CompletableFuture.completedFuture(getPlayer(userUUID)?.username)
    }
}

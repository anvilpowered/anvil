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
import org.anvilpowered.anvil.common.util.CommonUserService
import java.util.Optional
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.function.Function
import java.util.stream.Collectors

class VelocityUserService @Inject constructor() : CommonUserService<Player?, Player?>(
    Player::class.java) {
    @Inject
    private val proxyServer: ProxyServer? = null
    override fun get(userName: String): Optional<Player?>? {
        return proxyServer!!.getPlayer(userName)
    }

    override fun get(userUUID: UUID): Optional<Player?>? {
        return proxyServer!!.getPlayer(userUUID)
    }

    override fun getPlayer(userName: String): Optional<Player?>? {
        return get(userName)
    }

    override fun getPlayer(userUUID: UUID): Optional<Player?>? {
        return get(userUUID)
    }

    override fun getPlayer(player: Player): Optional<Player> {
        return Optional.of(player)
    }

    override fun matchPlayerNames(startsWith: String): List<String> {
        val startsWithLowerCase = startsWith.toLowerCase()
        return onlinePlayers.stream()
            .map { obj: Player -> obj.username }
            .filter { name: String -> name.toLowerCase().startsWith(startsWithLowerCase) }
            .collect(Collectors.toList())
    }

    override fun getOnlinePlayers(): Collection<Player> {
        return proxyServer!!.allPlayers
    }

    override fun getUUID(userName: String): CompletableFuture<Optional<UUID>> {
        val userUUID = getPlayer(userName).map<UUID>(Function { obj: Player -> obj.uniqueId })
        return if (userUUID.isPresent) {
            CompletableFuture.completedFuture(userUUID)
        } else super.getUUID(userName)
    }

    override fun getUserName(userUUID: UUID): CompletableFuture<Optional<String>> {
        val userName = getPlayer(userUUID).map<String>(Function { obj: Player -> obj.username })
        return if (userName.isPresent) {
            CompletableFuture.completedFuture(userName)
        } else super.getUserName(userUUID)
    }

    override fun getUUID(player: Player): UUID {
        return player.uniqueId
    }

    override fun getUserName(player: Player): String {
        return player.username
    }
}

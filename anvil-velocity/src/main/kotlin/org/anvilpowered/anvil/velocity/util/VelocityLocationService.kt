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

package org.anvilpowered.anvil.velocity.util

import com.google.inject.Inject
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import java.util.Optional
import java.util.UUID
import java.util.concurrent.CompletableFuture
import org.anvilpowered.anvil.api.util.UserService
import org.anvilpowered.anvil.common.util.CommonLocationService

class VelocityLocationService : CommonLocationService() {

    @Inject
    private lateinit var userService: UserService<Player, Player>

    @Inject
    private lateinit var proxyServer: ProxyServer

    override fun getServerName(userUUID: UUID): Optional<String> {
        return userService.getPlayer(userUUID)
            .flatMap { it.currentServer }
            .map { it.serverInfo.name }
    }

    override fun getServerName(userName: String): Optional<String> {
        return userService.getPlayer(userName)
            .flatMap { it.currentServer }
            .map { it.serverInfo.name }
    }

    private fun setServer(player: Player, serverName: String): CompletableFuture<Boolean> {
        return proxyServer.getServer(serverName)
            .map { player.createConnectionRequest(it).connect() }
            .map { it.thenApply { c -> c.isSuccessful } }
            .orElse(CompletableFuture.completedFuture(false))
    }

    override fun setServer(userUUID: UUID, serverName: String): CompletableFuture<Boolean> {
        return userService.getPlayer(userUUID)
            .map { setServer(it, serverName) }
            .orElse(CompletableFuture.completedFuture(false))
    }

    override fun setServer(userName: String, serverName: String): CompletableFuture<Boolean> {
        return userService.getPlayer(userName)
            .map { setServer(it, serverName) }
            .orElse(CompletableFuture.completedFuture(false))
    }
}

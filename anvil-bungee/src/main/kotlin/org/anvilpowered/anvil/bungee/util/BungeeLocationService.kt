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

package org.anvilpowered.anvil.bungee.util

import com.google.inject.Inject
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.connection.ProxiedPlayer
import org.anvilpowered.anvil.api.util.UserService
import org.anvilpowered.anvil.common.util.CommonLocationService
import java.util.Optional
import java.util.UUID
import java.util.concurrent.CompletableFuture

class BungeeLocationService : CommonLocationService() {

    @Inject
    private lateinit var userService: UserService<ProxiedPlayer, ProxiedPlayer>

    override fun getServerName(userUUID: UUID): Optional<String> {
        return userService.getPlayer(userUUID).map { it.server.info.name }
    }

    override fun getServerName(userName: String): Optional<String> {
        return userService.getPlayer(userName).map { it.server.info.name }
    }

    override fun getAvailableServerNames(): MutableList<String> {
        val serverNames = ArrayList<String>()
        for (server in ProxyServer.getInstance().servers) {
            serverNames.add(server.key)
        }
        return serverNames
    }

    private fun setServer(player: ProxiedPlayer, serverName: String): CompletableFuture<Boolean> {
        val server = ProxyServer.getInstance().getServerInfo(serverName)
            ?: return CompletableFuture.completedFuture(false)
        val future = CompletableFuture<Boolean>()
        player.connect(server) { success, _ -> future.complete(success) }
        return future
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

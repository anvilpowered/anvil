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

package org.anvilpowered.anvil.velocity.util;

import com.google.inject.Inject;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import org.anvilpowered.anvil.common.util.CommonUserService;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class VelocityUserService extends CommonUserService<Player, Player> {

    @Inject
    private ProxyServer proxyServer;

    @Override
    public Optional<Player> get(String userName) {
        return proxyServer.getPlayer(userName);
    }

    @Override
    public Optional<Player> get(UUID userUUID) {
        return proxyServer.getPlayer(userUUID);
    }

    @Override
    public Optional<Player> getPlayer(String userName) {
        return get(userName);
    }

    @Override
    public Optional<Player> getPlayer(UUID userUUID) {
        return get(userUUID);
    }

    @Override
    public Collection<Player> getOnlinePlayers() {
        return proxyServer.getAllPlayers();
    }

    @Override
    public CompletableFuture<Optional<UUID>> getUUID(String userName) {
        Optional<UUID> userUUID = getPlayer(userName).map(Player::getUniqueId);
        if (userUUID.isPresent()) {
            return CompletableFuture.completedFuture(userUUID);
        }
        return super.getUUID(userName);
    }

    @Override
    public CompletableFuture<Optional<String>> getUserName(UUID userUUID) {
        Optional<String> userName = getPlayer(userUUID).map(Player::getUsername);
        if (userName.isPresent()) {
            return CompletableFuture.completedFuture(userName);
        }
        return super.getUserName(userUUID);
    }

    @Override
    public UUID getUUID(Player player) {
        return player.getUniqueId();
    }

    @Override
    public String getUserName(Player player) {
        return player.getUsername();
    }
}

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

package org.anvilpowered.anvil.bungee.util;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.anvilpowered.anvil.common.util.CommonUserService;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BungeeUserService extends CommonUserService<ProxiedPlayer, ProxiedPlayer> {

    @Override
    public Optional<ProxiedPlayer> get(String userName) {
        return Optional.of(ProxyServer.getInstance().getPlayer(userName));
    }

    @Override
    public Optional<ProxiedPlayer> get(UUID userUUID) {
        return Optional.of(ProxyServer.getInstance().getPlayer(userUUID));
    }

    @Override
    public Optional<ProxiedPlayer> getPlayer(String userName) {
        return get(userName);
    }

    @Override
    public Optional<ProxiedPlayer> getPlayer(UUID userUUID) {
        return get(userUUID);
    }

    @Override
    public Optional<ProxiedPlayer> getPlayer(ProxiedPlayer proxiedPlayer) {
        return Optional.of(proxiedPlayer);
    }

    @Override
    public Collection<ProxiedPlayer> getOnlinePlayers() {
        return ProxyServer.getInstance().getPlayers();
    }

    @Override
    public CompletableFuture<Optional<UUID>> getUUID(String userName) {
        Optional<UUID> userUUID = getPlayer(userName).map(ProxiedPlayer::getUniqueId);
        if (userUUID.isPresent()) {
            return CompletableFuture.completedFuture(userUUID);
        }
        return super.getUUID(userName);
    }

    @Override
    public CompletableFuture<Optional<String>> getUserName(UUID userUUID) {
        Optional<String> userName = getPlayer(userUUID).map(ProxiedPlayer::getName);
        if (userName.isPresent()) {
            return CompletableFuture.completedFuture(userName);
        }
        return super.getUserName(userUUID);
    }

    @Override
    public UUID getUUID(ProxiedPlayer proxiedPlayer) {
        return proxiedPlayer.getUniqueId();
    }

    @Override
    public String getUserName(ProxiedPlayer proxiedPlayer) {
        return proxiedPlayer.getDisplayName();
    }
}

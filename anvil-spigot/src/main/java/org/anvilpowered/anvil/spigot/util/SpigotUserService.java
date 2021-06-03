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

package org.anvilpowered.anvil.spigot.util;

import com.google.inject.Inject;
import org.anvilpowered.anvil.common.util.CommonUserService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpigotUserService extends CommonUserService<Player, Player> {

    @Inject
    public SpigotUserService() {
        super(Player.class);
    }

    @Override
    public Optional<Player> get(String userName) {
        return Optional.ofNullable(Bukkit.getPlayer(userName));
    }

    @Override
    public Optional<Player> get(UUID userUUID) {
        return Optional.ofNullable(Bukkit.getPlayer(userUUID));
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
    public Optional<Player> getPlayer(Player player) {
        return Optional.of(player);
    }

    @Override
    public List<String> matchPlayerNames(String startsWith) {
        String startsWithLowerCase = startsWith.toLowerCase();
        return Stream.of(Bukkit.getOfflinePlayers())
            .map(OfflinePlayer::getName)
            .filter(Objects::nonNull)
            .filter(name -> name.toLowerCase().startsWith(startsWithLowerCase))
            .collect(Collectors.toList());
    }

    @Override
    public Collection<Player> getOnlinePlayers() {
        return (Collection<Player>) Bukkit.getOnlinePlayers();
    }

    @Override
    public CompletableFuture<Optional<UUID>> getUUID(String userName) {
        return CompletableFuture.completedFuture(getPlayer(userName).map(Player::getUniqueId));
    }

    @Override
    public CompletableFuture<Optional<String>> getUserName(UUID userUUID) {
        return CompletableFuture.completedFuture(getPlayer(userUUID).map(Player::getName));
    }

    @Override
    public UUID getUUID(Player user) {
        return user.getUniqueId();
    }

    @Override
    public String getUserName(Player user) {
        return user.getName();
    }
}

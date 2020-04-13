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

package org.anvilpowered.anvil.spigot.util;

import org.anvilpowered.anvil.api.util.UserService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class SpigotUserService implements UserService<Player, Player> {

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
    public Collection<Player> getOnlinePlayers() {
        return (Collection<Player>) Bukkit.getOnlinePlayers();
    }

    @Override
    public Optional<UUID> getUUID(String userName) {
        return get(userName).map(Entity::getUniqueId);
    }

    @Override
    public Optional<String> getUserName(UUID userUUID) {
        return get(userUUID).map(Player::getName);
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

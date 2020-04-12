/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.nukkit.util;

import cn.nukkit.Player;
import cn.nukkit.Server;
import org.anvilpowered.anvil.api.util.UserService;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class NukkitUserService implements UserService<Player, Player> {

    @Override
    public Optional<Player> get(String userName) {
        return Optional.of(Server.getInstance().getPlayer(userName));
    }

    @Override
    public Optional<Player> get(UUID userUUID) {
        return Server.getInstance().getPlayer(userUUID);
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
        return Server.getInstance().getOnlinePlayers().values();
    }

    @Override
    public Optional<UUID> getUUID(String userName) {
        return Optional.ofNullable(Server.getInstance().getPlayer(userName)).map(Player::getUniqueId);
    }

    @Override
    public Optional<String> getUserName(UUID userUUID) {
        return Server.getInstance().getPlayer(userUUID).map(Player::getDisplayName);
    }

    @Override
    public UUID getUUID(Player player) {
        return player.getUniqueId();
    }

    @Override
    public String getUserName(Player player) {
        return player.getDisplayName();
    }
}

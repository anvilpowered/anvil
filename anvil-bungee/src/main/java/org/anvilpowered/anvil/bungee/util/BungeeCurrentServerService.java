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

import com.google.inject.Inject;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.anvilpowered.anvil.api.util.CurrentServerService;
import org.anvilpowered.anvil.api.util.UserService;

import java.util.Optional;
import java.util.UUID;

public class BungeeCurrentServerService implements CurrentServerService {

    @Inject
    private UserService<ProxiedPlayer, ProxiedPlayer> userService;

    @Override
    public Optional<String> getName(UUID userUUID) {
        return userService.getPlayer(userUUID)
            .map(proxiedPlayer -> proxiedPlayer.getServer().getInfo().getName());
    }

    @Override
    public Optional<String> getName(String userName) {
        return userService.getPlayer(userName)
            .map(proxiedPlayer -> proxiedPlayer.getServer().getInfo().getName());
    }
}

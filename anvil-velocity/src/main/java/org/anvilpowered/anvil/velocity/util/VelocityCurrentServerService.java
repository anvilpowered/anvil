/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020
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

package org.anvilpowered.anvil.velocity.util;

import com.velocitypowered.api.proxy.Player;
import org.anvilpowered.anvil.api.util.CurrentServerService;
import org.anvilpowered.anvil.api.util.UserService;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

public class VelocityCurrentServerService implements CurrentServerService {

    @Inject
    protected UserService<Player, Player> userService;

    @Override
    public Optional<String> getName(UUID userUUID) {
        return userService.get(userUUID).flatMap(Player::getCurrentServer).map(s -> s.getServerInfo().getName());
    }

    @Override
    public Optional<String> getName(String userName) {
        return userService.get(userName).flatMap(Player::getCurrentServer).map(s -> s.getServerInfo().getName());
    }
}

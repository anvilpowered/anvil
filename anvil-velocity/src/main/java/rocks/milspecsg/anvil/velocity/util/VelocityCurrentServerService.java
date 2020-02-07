/*
 *   Anvil - MilSpecSG
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

package rocks.milspecsg.anvil.velocity.util;

import com.velocitypowered.api.proxy.Player;
import rocks.milspecsg.anvil.api.util.CurrentServerService;
import rocks.milspecsg.anvil.api.util.UserService;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

public class VelocityCurrentServerService implements CurrentServerService {

    @Inject
    UserService<Player, Player> userService;

    @Override
    public Optional<String> getCurrentServerName(UUID userUUID) {
        return userService.get(userUUID).flatMap(Player::getCurrentServer).map(s -> s.getServerInfo().getName());
    }
}

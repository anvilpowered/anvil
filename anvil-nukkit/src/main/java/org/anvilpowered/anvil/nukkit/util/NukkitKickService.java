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
import com.google.inject.Inject;
import org.anvilpowered.anvil.api.util.KickService;
import org.anvilpowered.anvil.api.util.UserService;

import java.util.UUID;

public class NukkitKickService  implements KickService {

    @Inject
    private UserService<Player, Player> userService;

    private String getReason(Object reason) {
        return reason.toString();
    }

    @Override
    public void kick(UUID userUUID, Object reason) {
        userService.getPlayer(userUUID).ifPresent(player -> player.kick(reason.toString()));
    }

    @Override
    public void kick(String userName, Object reason) {
        userService.getPlayer(userName).ifPresent(player -> player.kick(reason.toString()));
    }

    @Override
    public void kick(UUID userUUID) {
        kick(userUUID, "You have been kicked");
    }

    @Override
    public void kick(String userName) {
        kick(userName, "You have been kicked");
    }
}

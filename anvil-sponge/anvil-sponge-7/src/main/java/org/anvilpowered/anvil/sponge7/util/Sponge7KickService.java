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

package org.anvilpowered.anvil.sponge7.util;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.util.KickService;
import org.anvilpowered.anvil.api.util.UserService;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import java.util.UUID;

public class Sponge7KickService implements KickService {

    @Inject
    private UserService<User, Player> userService;

    @Override
    public void kick(UUID userUUID, Object reason) {
        userService.getPlayer(userUUID).ifPresent(player -> player.kick(Text.of(reason)));
    }

    @Override
    public void kick(String userName, Object reason) {
        userService.getPlayer(userName).ifPresent(player -> player.kick(Text.of(reason)));
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

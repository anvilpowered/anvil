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

package org.anvilpowered.anvil.spigot.util;

import net.md_5.bungee.api.chat.TextComponent;
import org.anvilpowered.anvil.api.util.KickService;
import org.anvilpowered.anvil.api.util.UserService;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.UUID;

public class SpigotKickService implements KickService {

    @Inject
    private UserService<Player, Player> userService;

    private TextComponent getReason(Object reason) {
        return reason instanceof TextComponent ? (TextComponent) reason : new TextComponent(reason.toString());
    }

    @Override
    public void kick(UUID userUUID, Object reason) {
        userService.getPlayer(userUUID).ifPresent(player -> player.kickPlayer(reason.toString()));
    }

    @Override
    public void kick(String userName, Object reason) {
        userService.getPlayer(userName).ifPresent(player -> player.kickPlayer(reason.toString()));
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

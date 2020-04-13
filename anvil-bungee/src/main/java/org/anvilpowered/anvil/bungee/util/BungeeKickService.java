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
import net.md_5.bungee.api.chat.TextComponent;
import org.anvilpowered.anvil.api.util.KickService;

import java.util.UUID;

public class BungeeKickService implements KickService {

    private TextComponent getReason(Object reason) {
        return reason instanceof TextComponent ? (TextComponent) reason : new TextComponent(reason.toString());
    }

    @Override
    public void kick(UUID userUUID, Object reason) {
        ProxyServer.getInstance().getPlayer(userUUID).disconnect((getReason(reason)));
    }

    @Override
    public void kick(String userName, Object reason) {
        ProxyServer.getInstance().getPlayer(userName).disconnect((getReason(reason)));
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

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

package org.anvilpowered.anvil.core.spigot.listeners;

import com.google.inject.Inject;
import org.anvilpowered.anvil.core.api.coremember.CoreMemberManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class SpigotPlayerListener implements Listener {

    @Inject
    CoreMemberManager coreMemberManager;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        coreMemberManager.getPrimaryComponent()
            .getOneOrGenerateForUser(
                player.getUniqueId(),
                player.getName(),
                player.getAddress().getHostString()
            );
        org.anvilpowered.anvil.api.event.events.player.PlayerJoinEvent anvilEvent = new org.anvilpowered.anvil.api.event.events.player.PlayerJoinEvent() {
            @Override
            public org.anvilpowered.anvil.api.Interface.entity.Player.Player getPlayer() {
                return null;
            }
        };
    }
}

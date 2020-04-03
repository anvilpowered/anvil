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

package org.anvilpowered.anvil.bungee.listener;

import com.google.inject.Inject;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.anvilpowered.anvil.api.core.coremember.CoreMemberManager;
import org.anvilpowered.anvil.api.core.model.coremember.CoreMember;
import org.anvilpowered.anvil.api.core.plugin.PluginMessages;
import org.anvilpowered.anvil.api.data.key.Keys;
import org.anvilpowered.anvil.api.data.registry.Registry;

public class BungeePlayerListener implements Listener {

    @Inject
    private CoreMemberManager coreMemberManager;

    @Inject
    private PluginMessages<TextComponent> pluginMessages;

    @Inject
    private Registry registry;

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        if (registry.getOrDefault(Keys.PROXY_MODE)) {
            return;
        }
        ProxiedPlayer player = event.getPlayer();
        coreMemberManager.getPrimaryComponent()
            .getOneOrGenerateForUser(
                player.getUniqueId(),
                player.getDisplayName(),
                player.getAddress().getHostName()
            ).thenAcceptAsync(optionalMember -> {
            if (!optionalMember.isPresent()) {
                return;
            }
            CoreMember<?> coreMember = optionalMember.get();
            if (coreMemberManager.getPrimaryComponent().checkBanned(coreMember)) {
                player.disconnect(
                    pluginMessages.getBanMessage(coreMember.getBanReason(), coreMember.getBanEndUtc())
                );
            }
        }).join();
    }

    @EventHandler
    public void onPlayerChat(ChatEvent event) {
        if (registry.getOrDefault(Keys.PROXY_MODE)) {
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        coreMemberManager.getPrimaryComponent()
            .getOneForUser(
                player.getUniqueId()
            ).thenAcceptAsync(optionalMember -> {
            if (!optionalMember.isPresent()) {
                return;
            }
            CoreMember<?> coreMember = optionalMember.get();
            if (coreMemberManager.getPrimaryComponent().checkMuted(coreMember)) {
                event.setCancelled(true);
                player.sendMessage(
                    pluginMessages.getMuteMessage(coreMember.getMuteReason(), coreMember.getMuteEndUtc())
                );
            }
        }).join();
    }
}

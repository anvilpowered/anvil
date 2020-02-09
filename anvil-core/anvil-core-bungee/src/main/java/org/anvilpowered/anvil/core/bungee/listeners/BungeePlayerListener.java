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

package org.anvilpowered.anvil.core.bungee.listeners;

import com.google.inject.Inject;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.anvilpowered.anvil.core.api.coremember.CoreMemberManager;
import org.anvilpowered.anvil.core.api.model.coremember.CoreMember;
import org.anvilpowered.anvil.core.api.plugin.PluginMessages;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class BungeePlayerListener implements Listener {

    @Inject
    CoreMemberManager coreMemberManager;

    @Inject
    PluginMessages<TextComponent> pluginMessages;


    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
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
            if (coreMember.isBanned() && coreMember.getBanEndUtc().isAfter(OffsetDateTime.now(ZoneOffset.UTC).toInstant())) {
                player.disconnect(pluginMessages.getBanMessage(coreMember.getBanReason(), coreMember.getBanEndUtc()));
            } else if (coreMember.isBanned()) {
                coreMemberManager.getPrimaryComponent().unBanUser(player.getUniqueId());
            }
        }).join();
    }

    @EventHandler
    public void onPlayerChat(ChatEvent event) {
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        coreMemberManager.getPrimaryComponent()
            .getOneForUser(
                player.getUniqueId()
            ).thenAcceptAsync(optionalMember -> {
            if (!optionalMember.isPresent()) {
                return;
            }
            CoreMember<?> member = optionalMember.get();
            if (member.isMuted() && member.getMuteEndUtc().isAfter(OffsetDateTime.now(ZoneOffset.UTC).toInstant())) {
                event.setCancelled(true);
                player.sendMessage(pluginMessages.getMuteMessage(member.getMuteReason(), member.getMuteEndUtc()));
            } else if (member.isMuted()) {
                coreMemberManager.getPrimaryComponent().unMuteUser(player.getUniqueId());
            }
        }).join();
    }
}

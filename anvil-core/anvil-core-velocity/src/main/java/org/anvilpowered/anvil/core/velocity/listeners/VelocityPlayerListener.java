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

package org.anvilpowered.anvil.core.velocity.listeners;

import com.google.inject.Inject;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import net.kyori.text.TextComponent;
import org.anvilpowered.anvil.core.api.coremember.CoreMemberManager;
import org.anvilpowered.anvil.core.api.model.coremember.CoreMember;
import org.anvilpowered.anvil.core.api.plugin.PluginMessages;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class VelocityPlayerListener {

    @Inject
    CoreMemberManager coreMemberManager;

    @Inject
    PluginMessages<TextComponent> pluginMessages;

    @Subscribe
    public void onPlayerJoin(LoginEvent event) {
        Player player = event.getPlayer();
        coreMemberManager.getPrimaryComponent()
            .getOneOrGenerateForUser(
                player.getUniqueId(),
                player.getUsername(),
                player.getRemoteAddress().getHostString()
            ).thenAcceptAsync(optionalMember -> {
            if (!optionalMember.isPresent()) {
                return;
            }
            CoreMember<?> member = optionalMember.get();
            if (member.isBanned() && member.getBanEndUtc().isAfter(OffsetDateTime.now(ZoneOffset.UTC).toInstant())) {
                event.setResult(ResultedEvent.ComponentResult.denied(pluginMessages.getBanMessage(member.getBanReason(), member.getBanEndUtc())));
            } else if (member.isBanned()) {
                coreMemberManager.getPrimaryComponent().unBanUser(player.getUniqueId());
            }
        }).join();
    }

    @Subscribe
    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        coreMemberManager.getPrimaryComponent()
            .getOneForUser(
                player.getUniqueId()
            ).thenAcceptAsync(optionalMember -> {
            if (!optionalMember.isPresent()) {
                return;
            }
            CoreMember<?> member = optionalMember.get();
            if (member.isMuted() && member.getMuteEndUtc().isAfter(OffsetDateTime.now(ZoneOffset.UTC).toInstant())) {
                event.setResult(PlayerChatEvent.ChatResult.denied());
                player.sendMessage(pluginMessages.getMuteMessage(member.getMuteReason(), member.getMuteEndUtc()));
            } else if (member.isMuted()){
                coreMemberManager.getPrimaryComponent().unMuteUser(player.getUniqueId());
            }
        }).join();
    }
}

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

package org.anvilpowered.anvil.velocity.listeners;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import net.kyori.text.TextComponent;
import org.anvilpowered.anvil.api.core.coremember.CoreMemberManager;
import org.anvilpowered.anvil.api.core.model.coremember.CoreMember;
import org.anvilpowered.anvil.api.core.plugin.PluginMessages;

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
            CoreMember<?> coreMember = optionalMember.get();
            if (coreMemberManager.getPrimaryComponent().checkBanned(coreMember)) {
                player.disconnect(
                    pluginMessages.getBanMessage(coreMember.getBanReason(), coreMember.getBanEndUtc())
                );
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
            CoreMember<?> coreMember = optionalMember.get();
            if (coreMemberManager.getPrimaryComponent().checkMuted(coreMember)) {
                event.setResult(PlayerChatEvent.ChatResult.denied());
                player.sendMessage(
                    pluginMessages.getMuteMessage(coreMember.getMuteReason(), coreMember.getMuteEndUtc())
                );
            }
        }).join();
    }
}

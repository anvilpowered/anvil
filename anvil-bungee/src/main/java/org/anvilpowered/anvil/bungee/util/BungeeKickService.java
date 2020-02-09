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

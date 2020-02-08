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

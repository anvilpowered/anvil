package org.anvilpowered.anvil.bungee.util;

import com.google.inject.Inject;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.anvilpowered.anvil.api.util.CurrentServerService;
import org.anvilpowered.anvil.api.util.UserService;

import java.util.Optional;
import java.util.UUID;

public class BungeeCurrentServerService implements CurrentServerService {

    @Inject
    private UserService<ProxiedPlayer, ProxiedPlayer> userService;

    @Override
    public Optional<String> getName(UUID userUUID) {
        return userService.getPlayer(userUUID)
            .map(proxiedPlayer -> proxiedPlayer.getServer().getInfo().getName());
    }

    @Override
    public Optional<String> getName(String userName) {
        return userService.getPlayer(userName)
            .map(proxiedPlayer -> proxiedPlayer.getServer().getInfo().getName());
    }
}
